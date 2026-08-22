package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa

class Menu {
    // Referencia das listas para poder imprimi-las
    List<Candidato> candidatos
    List<Empresa> empresas

    // Método principal
    void iniciar() {
        Scanner scanner = new Scanner(System.in)
        int opcao = 0

        while (opcao != 3) {
            println "\n=========================="
            println "    MENU LINKETINDER    "
            println "=========================="
            println "1. Listar Empresas"
            println "2. Listar Candidatos"
            println "3. Sair"
            print "Escolha uma opção: "

            try {
                opcao = scanner.nextInt()

                switch (opcao) {
                    case 1:
                        println "\n=== EMPRESAS CADASTRADA (${empresas.size()}) ==="
                        empresas.each {it.exibirPerfil()}
                        break
                    case 2:
                        println "\n=== EMPRESAS CADASTRADAS (${candidatos.size()}) ==="
                        candidatos.each {it.exibirPerfil()}
                        break
                    case 3:
                        println "\nEncerrando o sistema..."
                        break
                    default:
                        println "\nOpção inválida! Escolha 1, 2 ou 3."
                }
            } catch(InputMismatchException e) {
                println "\nErro: Por favor, digite apenas números inteiros."
                scanner.nextLine() // Limpa o buffer do teclado
            }
        }
    }
}
