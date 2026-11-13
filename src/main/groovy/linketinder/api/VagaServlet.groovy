package linketinder.api

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import linketinder.controller.VagaController
import linketinder.model.Vaga
import linketinder.model.Competencia

@WebServlet(name = "VagaServlet", urlPatterns = "/api/vagas")
class VagaServlet extends HttpServlet {

    private VagaController vagaController

    @Override
    void init() {
        // Servlet não crie o DAO
        this.vagaController = new VagaController()
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setStatus(HttpServletResponse.SC_NO_CONTENT)
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        // Garante que os acentos do JSON cheguem corretos (UTF-8)
        request.setCharacterEncoding("UTF-8")
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            String jsonRecebido = request.getReader().getText()
            println "\n---> [Servlet] JSON Recebido em /api/vagas:"
            println jsonRecebido

            def parser = new JsonSlurper()
            def dados = parser.parseText(jsonRecebido)

            Vaga novaVaga = new Vaga()
            // 'idEmpresa' conforme o nome do campo na sua Model Vaga
            novaVaga.idEmpresa = dados.idEmpresa as Integer
            novaVaga.nome = dados.titulo
            novaVaga.descricao = dados.descricao
            novaVaga.local = dados.local

            if (dados.competencias) {
                dados.competencias.each { skill ->
                    novaVaga.competencias.add(new Competencia(nome: skill.toString()))
                }
            }

            boolean sucesso = vagaController.cadastrar(novaVaga)

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_CREATED) // 201
                response.getWriter().write(JsonOutput.toJson([mensagem: "Vaga cadastrada via Servlet com sucesso!"]))
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST) // 400
                response.getWriter().write(JsonOutput.toJson([erro: "Falha na validacao das regras de negócio para Vagas."]))
            }

        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) // 500
            response.getWriter().write(JsonOutput.toJson([erro: "Erro interno no Servlet: " + e.message]))
        }
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "Content-Type")
    }
}