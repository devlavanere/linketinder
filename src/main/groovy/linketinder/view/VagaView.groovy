package linketinder.view

import linketinder.app.GerenciadorDePerfis
import linketinder.model.Vaga

class VagaView {
    private GerenciadorDePerfis gerenciador
    private Scanner scanner

    VagaView(GerenciadorDePerfis gerenciador, Scanner scanner) {
        this.gerenciador = gerenciador
        this.scanner = scanner
    }

    void listar() {
        def vagas = gerenciador.listarVagas()
        println "\n=== VAGAS NO BANCO (${vagas.size()}) ==="
        vagas.each { it.exibirVaga() }
    }

    void cadastrar() {
        println "\n--- WIZARD DE CADASTRO DE VAGA ---"
        Vaga nova = new Vaga()

        nova.idEmpresa = lerIntSeguro("ID da Empresa contratante (ex: 1 para Pastelsoft): ")
        nova.nome = lerEntradaObrigatoria("Título da Vaga: ")
        nova.descricao = lerEntradaObrigatoria("Descrição da Vaga: ")
        nova.local = lerEntradaObrigatoria("Local (ex: Remoto, São Paulo): ")

        gerenciador.adicionarVaga(nova)
        println "\nVaga '${nova.nome}' cadastrada com sucesso!"
    }

    void deletar() {
        println "\n--- EXCLUIR VAGA ---"
        int idVaga = lerIntSeguro("Digite o ID Numérico da Vaga que deseja excluir (ou 0 para cancelar): ")
        if (idVaga != 0) {
            gerenciador.deletarVaga(idVaga)
        }
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE VALIDAÇÃO
    // ==========================================
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