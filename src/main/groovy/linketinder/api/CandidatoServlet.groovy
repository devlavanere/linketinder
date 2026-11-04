package linketinder.api

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import linketinder.controller.CandidatoController
import linketinder.model.Candidato
import linketinder.model.Competencia

@WebServlet(name = "CandidatoServlet", urlPatterns = "/api/candidatos")
class CandidatoServlet extends HttpServlet {

    private CandidatoController candidatoController

    @Override
    void init() {
        // O init() roda uma única vez quando o Tomcat sobe o Servlet
        this.candidatoController = new CandidatoController()
    }

    // 2. TRATATIVA DE CORS (Para o seu Frontend TypeScript não chiar)
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setStatus(HttpServletResponse.SC_NO_CONTENT)
    }

    // 3. O VERBO HTTP POST (Cadastrar)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            // Lê o JSON do corpo da requisição
            String jsonRecebido = request.getReader().getText()

            println "\n---> [Servlet] JSON Recebido em /api/candidatos:"
            println jsonRecebido

            def parser = new JsonSlurper()
            def dados = parser.parseText(jsonRecebido)

            Candidato novo = new Candidato()
            novo.nome = dados.nome
            novo.sobrenome = dados.sobrenome
            novo.email = dados.email
            novo.cpf = dados.cpf
            novo.senha = dados.senha
            novo.pais = dados.pais
            novo.cep = dados.cep
            novo.descricao = dados.descricao

            if (dados.dataNascimento) {
                novo.dataNascimento = LocalDate.parse(dados.dataNascimento.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            if (dados.competencias) {
                dados.competencias.each { skill ->
                    novo.competencias.add(new Competencia(nome: skill.toString()))
                }
            }

            boolean sucesso = candidatoController.cadastrar(novo)

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_CREATED) // HTTP 201
                response.getWriter().write(JsonOutput.toJson([mensagem: "Candidato cadastrado via Servlet com sucesso!"]))
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST) // HTTP 400
                response.getWriter().write(JsonOutput.toJson([erro: "Falha na validacao das regras de negócio do Linketinder."]))
            }

        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) // HTTP 500
            response.getWriter().write(JsonOutput.toJson([erro: "Erro interno no Servlet: " + e.message]))
        }
    }

    // Método auxiliar para configurar o CORS
    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "Content-Type")
    }
}