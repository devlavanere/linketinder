package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa
import java.util.Scanner

class Menu {
    // Referencia das listas para poder imprimi-las
    List<Candidato> candidatos
    List<Empresa> empresas

    // Método principal
    void iniciar() {
        Scanner scanner = new Scanner(System.in)
        int opcao = 0

        while (opcao != 5) {
            println "\n=========================="
            println "    MENU LINKETINDER    "
            println "=========================="
            println "1. Listar Empresas"
            println "2. Listar Candidatos"
            println "3. Cadastrar Novo Candidato"
            println "4. Cadastrar Nova Empresa"
            println "5. Sair"
            print "Escolha uma opção: "

            try {
                opcao = scanner.nextInt()
                scanner.nextLine()

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
                        executarWizardCandidato(scanner)
                        break
                    case 4:
                        executarWizardEmpresa(scanner)
                        break
                    case 5:
                        println "\nEncerrando o sistema..."
                        break
                    default:
                        println "\nOpção inválida! Escolha 1, 2, 3, 4 ou 5."
                }
            } catch(InputMismatchException e) {
                println "\nErro: Por favor, digite apenas números inteiros."
                scanner.nextLine() // Limpa o buffer do teclado
            }
        }
    }
        private void executarWizardCandidato(Scanner scanner) {
            println "\n--- WIZARD DE CADASTRO DE CANDIDATO ---"
            Candidato novo = new Candidato()

            print "Nome: "
            novo.nome = scanner.nextLine()

            print "E-mail: "
            novo.email = scanner.nextLine()

            print "CPF: "
            novo.cpf = scanner.nextLine()

            print "Idade: "
            novo.idade = scanner.nextInt()
            scanner.nextLine() // Consumir o Enter

            print "Estado (UF): "
            novo.estado = scanner.nextLine()

            print "CEP: "
            novo.cep = scanner.nextLine()

            print "Descrição pessoal: "
            novo.descricao = scanner.nextLine()

            print "Competências (separadas por vírgula): "
            String compStr = scanner.nextLine()
            // Pega a string digitada, divide pelas vírgulas e remove espaços extras
            novo.competencias = compStr.split(',').collect { it.trim() }

            candidatos << novo
            println "=> Candidato(a) ${novo.nome} cadastrado(a) com sucesso!"
        }

        private void executarWizardEmpresa(Scanner scanner) {
            println "\n--- WIZARD DE CADASTRO DE EMPRESA ---"
            Empresa nova = new Empresa()

            print "Nome da Empresa: "
            nova.nome = scanner.nextLine()

            print "E-mail Corporativo: "
            nova.email = scanner.nextLine()

            print "CNPJ: "
            nova.cnpj = scanner.nextLine()

            print "País: "
            nova.pais = scanner.nextLine()

            print "Estado (UF): "
            nova.estado = scanner.nextLine()

            print "CEP: "
            nova.cep = scanner.nextLine()

            print "Descrição da empresa: "
            nova.descricao = scanner.nextLine()

            print "Competências desejadas (separadas por vírgula): "
            String compStr = scanner.nextLine()
            nova.competencias = compStr.split(',').collect { it.trim() }

            empresas << nova
            println "=> Empresa ${nova.nome} cadastrada com sucesso!"
        }
    }

