package linketinder.view

import linketinder.app.GerenciadorDePerfis
import linketinder.model.Empresa

class EmpresaView {
    private GerenciadorDePerfis gerenciador
    private Scanner scanner

    EmpresaView(GerenciadorDePerfis gerenciador, Scanner scanner) {
        this.gerenciador = gerenciador
        this.scanner = scanner
    }

    void listar() {
        def empresas = gerenciador.listarEmpresas()
        println "\n=== EMPRESAS NO BANCO (${empresas.size()}) ==="
        empresas.each { it.exibirPerfil() }
    }

    void cadastrar() {
        println "\n--- WIZARD DE CADASTRO DE EMPRESA ---"
        Empresa nova = new Empresa()

        nova.nome = lerEntradaObrigatoria("Nome da Empresa: ")
        nova.cnpj = lerEntradaObrigatoria("CNPJ (apenas números): ")
        nova.email = lerEntradaObrigatoria("E-mail Corporativo: ")
        nova.senha = lerEntradaObrigatoria("Senha (mínimo 6 caracteres): ")
        nova.pais = lerEntradaObrigatoria("País: ")
        nova.cep = lerEntradaObrigatoria("CEP: ")
        nova.descricao = lerEntradaObrigatoria("Descrição da empresa: ")

        gerenciador.adicionarEmpresa(nova)
        println "\nEmpresa ${nova.nome} cadastrada com sucesso!"
    }

    void deletar() {
        println "\n--- EXCLUIR EMPRESA ---"
        println "Atenção: Excluir uma empresa apagará automaticamente todas as vagas vinculadas a ela."
        String cnpj = lerEntradaObrigatoria("Digite o CNPJ da empresa que deseja excluir (ou 'cancelar'): ")
        if (cnpj.toLowerCase() != "cancelar") {
            gerenciador.deletarEmpresa(cnpj)
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
}