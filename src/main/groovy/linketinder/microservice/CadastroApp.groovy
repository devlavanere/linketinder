package linketinder.microservice

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import linketinder.controller.CandidatoController
import linketinder.model.Candidato
import linketinder.model.Competencia

import linketinder.controller.EmpresaController
import linketinder.model.Empresa

class CadastroApp {

    static void main(String[] args) {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0)

        server.createContext("/api/candidatos", new CandidatoHandler())
        server.createContext("/api/empresas", new EmpresaHandler())

        server.setExecutor(null)
        server.start()

        println "Microsserviço de Cadastro rodando na porta 8080..."
        println "Aguardando requisições JSON via HTTP POST..."
    }

    // --- MANIPULADOR PARA CADASTRO DE CANDIDATOS ---
    static class CandidatoHandler implements HttpHandler {
        private CandidatoController candidatoController = new CandidatoController()

        @Override
        void handle(HttpExchange exchange) throws IOException {
            // CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*")
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS")
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type")

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1)
                return
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody()
                    String jsonRecebido = new BufferedReader(new InputStreamReader(is)).getText()

                    println "\n---> JSON Recebido em /api/candidatos:"
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
                        String respostaOk = JsonOutput.toJson([mensagem: "Candidato cadastrado com sucesso no banco de dados!"])
                        enviarResposta(exchange, 201, respostaOk)
                    } else {
                        String respostaErro = JsonOutput.toJson([erro: "Falha na validacao das regras de negócio do Linketinder."])
                        enviarResposta(exchange, 400, respostaErro)
                    }

                } catch (Exception e) {
                    println "Erro interno: ${e.message}"
                    String erroJson = JsonOutput.toJson([erro: "Erro interno no servidor: " + e.message])
                    enviarResposta(exchange, 500, erroJson)
                }
            } else {
                exchange.sendResponseHeaders(405, -1)
            }
        }

        private void enviarResposta(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json")
            byte[] bytes = responseJson.getBytes("UTF-8")
            exchange.sendResponseHeaders(statusCode, bytes.length)
            OutputStream os = exchange.getResponseBody()
            os.write(bytes)
            os.close()
        }
    }

    // --- MANIPULADOR PARA CADASTRO DE EMPRESAS ---
    static class EmpresaHandler implements HttpHandler {
        private EmpresaController empresaController = new EmpresaController()

        @Override
        void handle(HttpExchange exchange) throws IOException {
            // CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*")
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS")
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type")

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1)
                return
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    InputStream is = exchange.getRequestBody()
                    String jsonRecebido = new BufferedReader(new InputStreamReader(is)).getText()

                    println "\n---> JSON Recebido em /api/empresas:"
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

                    if (dados.competencias) {
                        dados.competencias.each { skill ->
                            nova.competencias.add(new Competencia(nome: skill.toString()))
                        }
                    }

                    boolean sucesso = empresaController.cadastrar(nova)

                    if (sucesso) {
                        String respostaOk = JsonOutput.toJson([mensagem: "Empresa cadastrada com sucesso no banco de dados!"])
                        enviarResposta(exchange, 201, respostaOk)
                    } else {
                        String respostaErro = JsonOutput.toJson([erro: "Falha na validacao das regras de negócio do Linketinder."])
                        enviarResposta(exchange, 400, respostaErro)
                    }

                } catch (Exception e) {
                    println "Erro interno: ${e.message}"
                    String erroJson = JsonOutput.toJson([erro: "Erro interno: " + e.message])
                    enviarResposta(exchange, 500, erroJson)
                }
            } else {
                exchange.sendResponseHeaders(405, -1)
            }
        }

        private void enviarResposta(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json")
            byte[] bytes = responseJson.getBytes("UTF-8")
            exchange.sendResponseHeaders(statusCode, bytes.length)
            OutputStream os = exchange.getResponseBody()
            os.write(bytes)
            os.close()
        }
    }
}