package linketinder.view

import linketinder.app.GerenciadorDePerfis
import linketinder.model.Candidato
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CandidatoView {
    private GerenciadorDePerfis gerenciador
    private Scanner scanner

    CandidatoView(GerenciadorDePerfis gerenciador, Scanner scanner) {
        this.gerenciador = gerenciador
        this.scanner = scanner
    }

    void listar() {
        def candidatos = gerenciador.listarCandidatos()
        println "\n=== CANDIDATOS NO BANCO (${candidatos.size()}) ==="
        candidatos.each { println it }
    }

    void cadastrar() {
        println "\n--- WIZARD DE CADASTRO DE CANDIDATO ---"
        Candidato novo = new Candidato()

        novo.nome = lerEntradaObrigatoria("Nome: ")
        novo.sobrenome = lerEntradaObrigatoria("Sobrenome: ")
        novo.dataNascimento = lerDataObrigatoria("Data de Nascimento (DD/MM/AAAA): ")
        novo.email = lerEntradaObrigatoria("E-mail: ")
        novo.cpf = lerEntradaObrigatoria("CPF (apenas números): ")
        novo.senha = lerEntradaObrigatoria("Senha (mínimo 6 caracteres): ")
        novo.pais = lerEntradaObrigatoria("País: ")
        novo.cep = lerEntradaObrigatoria("CEP: ")
        novo.descricao = lerEntradaObrigatoria("Descrição pessoal: ")

        String skillsTexto = lerEntradaObrigatoria("Competências (separadas por vírgula. Ex: Java, Vue, SQL): ")
        skillsTexto.split(',').each { skillStr ->
            novo.competencias.add(new linketinder.model.Competencia(nome: skillStr.trim()))
        }

        try {
            gerenciador.adicionarCandidato(novo)
            println "\nCandidato(a) ${novo.nome} salvo com sucesso!"
        } catch(RuntimeException e) {
            println "\n Erro ao cadastrar candidato: ${e.getMessage()}"
        }
    }

    void deletar() {
        println "\n--- EXCLUIR CANDIDATO ---"
        String cpf = lerEntradaObrigatoria("Digite o CPF do candidato que deseja excluir (ou 'cancelar'): ")
        if (cpf.toLowerCase() != "cancelar") {
            try {
                gerenciador.deletarCandidato(cpf)
                println "Comando de deleção enviado."
            } catch(RuntimeException e) {
                println "\n Erro ao deletar: ${e.getMessage()}"
            }
        }
    }

    void curtirVaga() {
        println "\n--- APLICAR PARA VAGA (LIKE) ---"
        int idCandidato = lerIntSeguro("Confirme o seu ID de Candidato: ")
        int idVaga = lerIntSeguro("Digite o ID da Vaga que deseja curtir: ")

        try {
            boolean match = gerenciador.candidatoCurteVaga(idCandidato, idVaga)
            println "Vaga curtida com sucesso!"

            if (match) {
                println "\n IT'S A MATCH!"
                println "A empresa dona desta vaga já havia demonstrado interesse no seu perfil!"
            }
        } catch(RuntimeException e) {
            println "\n Erro ao curtir vaga: ${e.getMessage()}"
        }
    }

    // MÉTODOS AUXILIARES DE VALIDAÇÃO DO CONSOLE

    private String lerEntradaObrigatoria(String prompt) {
        String entrada = ""
        while (entrada.trim().isEmpty()) {
            print prompt
            entrada = scanner.nextLine()
            if (entrada.trim().isEmpty()) {
                println "Este campo é obrigatório. Por favor, não deixe em branco!"
            }
        }
        return entrada
    }

    private LocalDate lerDataObrigatoria(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        while (true) {
            print prompt
            String entrada = scanner.nextLine()
            try {
                return LocalDate.parse(entrada.trim(), formatter)
            } catch (DateTimeParseException e) {
                println "Formato inválido! Use o padrão DD/MM/AAAA (ex: 20/10/1990)."
            }
        }
    }

    private int lerIntSeguro(String prompt) {
        while (true) {
            print prompt
            String entrada = scanner.nextLine()
            try {
                return Integer.parseInt(entrada.trim())
            } catch (NumberFormatException e) {
                println "Erro: Por favor, digite apenas números inteiros!"
            }
        }
    }
}