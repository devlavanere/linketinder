package linketinder.service

import linketinder.dao.EmpresaDAO
import linketinder.model.Empresa
import spock.lang.Specification

class EmpresaServiceSpec extends Specification {

    def "Deve bloquear cadastro se o email corporativo for invalido"() {
        given: "Um Service e um DAO Mockado"
        def service = new EmpresaService()
        def daoMock = Mock(EmpresaDAO)
        service.dao = daoMock

        and: "Uma empresa com e-mail sem arroba"
        def empresa = new Empresa(
                nome: "Pastelsoft",
                email: "contato.pastelsoft.com", // Inválido
                cnpj: "12345678901234"
        )

        when: "Tentar cadastrar no Service"
        service.cadastrar(empresa)

        then: "O sistema deve barrar lançando a RuntimeException"
        def erro = thrown(RuntimeException)
        erro.message == "E-mail corporativo inválido."

        and: "O DAO nunca é chamado"
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CNPJ incorreto"() {
        given: "Um Service e um DAO Mockado"
        def service = new EmpresaService()
        def daoMock = Mock(EmpresaDAO)
        service.dao = daoMock

        and: "Uma empresa com CNPJ faltando números"
        def empresa = new Empresa(
                nome: "Pastelsoft",
                email: "contato@pastelsoft.com",
                cnpj: "12345" // Inválido
        )

        when: "Tentar cadastrar"
        service.cadastrar(empresa)

        then: "Deve barrar pelo CNPJ"
        def erro = thrown(RuntimeException)
        erro.message == "CNPJ inválido. Deve conter exatamente 14 números."
        0 * daoMock.inserir(_)
    }
}