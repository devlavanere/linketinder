package linketinder.view

import linketinder.app.GerenciadorDePerfis

class MenuPrincipal {
    private GerenciadorDePerfis gerenciador
    private Scanner scanner

    private CandidatoView candidatoView
    private EmpresaView empresaView
    private VagaView vagaView

    MenuPrincipal(GerenciadorDePerfis gerenciador) {
        this.gerenciador = gerenciador
        this.scanner = new Scanner(System.in)
        this.candidatoView = new CandidatoView(gerenciador, scanner)
        this.empresaView = new EmpresaView(gerenciador, scanner)
        this.vagaView = new VagaView(gerenciador, scanner)
    }

    void iniciar() {
        int opcao = 0
        while (opcao != 10) {
            exibirPainel()
            try {
                opcao = scanner.nextInt()
                scanner.nextLine() // Consome o Enter
                processarOpcao(opcao)
            } catch (InputMismatchException e) {
                println "\nErro: Por favor, digite apenas números inteiros."
                scanner.nextLine()
            }
        }
    }

    private void exibirPainel() {
        println "\n======================================"
        println "          MENU LINKETINDER          "
        println "======================================"
        println "--- LISTAGENS ---"
        println "1. Listar Empresas"
        println "2. Listar Candidatos"
        println "3. Listar Vagas"
        println "--- CADASTROS ---"
        println "4. Cadastrar Novo Candidato"
        println "5. Cadastrar Nova Empresa"
        println "6. Cadastrar Nova Vaga"
        println "--- EXCLUSÕES ---"
        println "7. Deletar Candidato (via CPF)"
        println "8. Deletar Empresa (via CNPJ)"
        println "9. Deletar Vaga (via ID)"
        println "--------------------------------------"
        println "10. Sair"
        print "Escolha uma opção: "
    }

    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                empresaView.listar();
                break
            case 2:
                candidatoView.listar();
                break
            case 3:
                vagaView.listar();
                break
            case 4:
                candidatoView.cadastrar();
                break
            case 5:
                empresaView.cadastrar();
                break
            case 6:
                vagaView.cadastrar();
                break
            case 7:
                candidatoView.deletar();
                break
            case 8:
                empresaView.deletar();
                break
            case 9:
                vagaView.deletar();
                break
            case 10:
                println "\nEncerrando o sistema de conexão JDBC...";
                break
            default:
                println "\nOpção inválida! Escolha de 1 a 10."
        }
    }
}