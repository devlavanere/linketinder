package linketinder.api

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import linketinder.controller.EmpresaController
import linketinder.model.Empresa

@WebServlet(name = "EmpresaServlet", urlPatterns = "/api/empresas")
class EmpresaServlet extends HttpServlet {

    private EmpresaController empresaController

    @Override
    void init() {
        this.empresaController = new EmpresaController()
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setStatus(HttpServletResponse.SC_NO_CONTENT)
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        setAccessControlHeaders(response)
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        try {
            String jsonRecebido = request.getReader().getText()
            println "\n---> [Servlet] JSON Recebido em /api/empresas:"
            println jsonRecebido

            def parser = new JsonSlurper()
            def dados = parser.parseText(jsonRecebido)

            Empresa nova = new Empresa()
            nova.nome = dados.nome
            nova.cnpj = dados.cnpj
            nova.email = dados.email
            nova.senha = dados.senha
            nova.pais = dados.pais
            nova.cep = dados.cep
            nova.descricao = dados.descricao

            boolean sucesso = empresaController.cadastrar(nova)

            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_CREATED) // 201
                response.getWriter().write(JsonOutput.toJson([mensagem: "Empresa cadastrada via Servlet com sucesso!"]))
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST) // 400
                response.getWriter().write(JsonOutput.toJson([erro: "Falha na validacao das regras de negócio do Linketinder."]))
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