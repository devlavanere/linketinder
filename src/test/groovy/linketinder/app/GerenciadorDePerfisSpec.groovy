/*Autor: Michel Lavanere Sampaio*/

package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa
import spock.lang.Specification

class GerenciadorDePerfisSpec extends Specification{
    def "deve adicionar um novo candidato na lista de candidatos com sucesso"() {
        given: "um gerenciador de perfis recém-criado e um novo candidato mockado"
        def gerenciador = new GerenciadorDePerfis()
        def candidato = new Candidato(nome: "Teste Candidato", cpf: "123.456.789-00", idade: 25)

        when: "o candidato é adicionado através do método"
        gerenciador.adicionarCandidato(candidato)

        then: "a lista de candidatos deve conter exatamente 1 elemento e ser o candidato correto"
        gerenciador.candidatos.size() == 1
        gerenciador.candidatos[0].nome == "Teste Candidato"
    }

    def "deve adicionar uma nova empresa na lista de empresas com sucesso"() {
        given: "um gerenciador de perfis e uma nova empresa mockada"
        def gerenciador = new GerenciadorDePerfis()
        def empresa = new Empresa(nome: "Teste Empresa", cnpj: "11.111.111/0001-11")

        when: "a empresa é adicionada"
        gerenciador.adicionarEmpresa(empresa)

        then: "a lista de empresas deve conter 1 elemento correspondente"
        gerenciador.empresas.size() == 1
        gerenciador.empresas[0].nome == "Teste Empresa"
    }
}

