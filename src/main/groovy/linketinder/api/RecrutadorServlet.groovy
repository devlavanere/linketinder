package linketinder.api

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import linketinder.controller.RecrutadorController
import linketinder.model.Recrutador

@WebServlet(name = "RecrutadorServlet", urlPatterns = "/api/recrutadores")
class RecrutadorServlet extends HttpServlet {

    private RecrutadorController recrutadorController

    @Override
    void init() {
        this.recrutadorController = new RecrutadorController()
    }

    // CORS Preflight
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setStatus(HttpServletResponse.SC_OK)
    }

    // POST /api/recrutadores — Cadastrar um novo recrutador
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            String jsonRecebido = request.getReader().getText()

            println "\n---> [Servlet] JSON Recebido em /api/recrutadores:"
            println jsonRecebido

            def parser = new JsonSlurper()
            def dados = parser.parseText(jsonRecebido)

            Recrutador novo = new Recrutador()
            novo.nome = dados.nome
            novo.email = dados.email
            novo.cpf = dados.cpf
            novo.senha = dados.senha
            novo.areaRecrutamento = dados.areaRecrutamento
            novo.descricao = dados.descricao
            novo.idEmpresa = dados.idEmpresa as Integer

            if (dados.dataNascimento) {
                novo.dataNascimento = LocalDate.parse(dados.dataNascimento.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }

            boolean sucesso = recrutadorController.cadastrar(novo)

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_CREATED)
                response.getWriter().write(JsonOutput.toJson([mensagem: "Recrutador cadastrado com sucesso!"]))
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
                response.getWriter().write(JsonOutput.toJson([erro: "Falha na validação das regras de negócio do Linketinder."]))
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            response.getWriter().write(JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            response.getWriter().write(JsonOutput.toJson([erro: "Erro interno no Servlet: " + e.message]))
        }
    }

    // GET /api/recrutadores?idEmpresa=X — Listar recrutadores de uma empresa (retorna DTOs)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            String idEmpresaStr = request.getParameter("idEmpresa")
            if (idEmpresaStr == null || idEmpresaStr.isEmpty() || idEmpresaStr.equalsIgnoreCase("NaN")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
                response.getWriter().write(JsonOutput.toJson([erro: "Parâmetro idEmpresa inválido"]))
                return
            }

            Integer idEmpresa = idEmpresaStr as Integer
            def listaDTOs = recrutadorController.listarPorEmpresa(idEmpresa)

            response.setStatus(HttpServletResponse.SC_OK)
            response.getWriter().write(JsonOutput.toJson(listaDTOs))

        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            response.getWriter().write(JsonOutput.toJson([erro: "Erro ao listar recrutadores: " + e.message]))
        }
    }

    // DELETE /api/recrutadores?id=X — Remover um recrutador
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            String idStr = request.getParameter("id")
            if (idStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
                response.getWriter().write(JsonOutput.toJson([erro: "Parâmetro 'id' é obrigatório."]))
                return
            }

            Integer id = idStr as Integer
            boolean sucesso = recrutadorController.deletar(id)

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT)
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
                response.getWriter().write(JsonOutput.toJson([erro: "Falha ao remover o recrutador."]))
            }

        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            response.getWriter().write(JsonOutput.toJson([erro: "Erro ao deletar recrutador: " + e.message]))
        }
    }

    // Método auxiliar para configurar o CORS
    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "Content-Type")
    }
}
