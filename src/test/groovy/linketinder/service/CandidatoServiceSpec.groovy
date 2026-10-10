package linketinder.service

import linketinder.dao.CandidatoDAO
import linketinder.dao.interfaces.ICrudDAO
import linketinder.model.Candidato
import spock.lang.Specification
import java.time.LocalDate

class CandidatoServiceSpec extends Specification {

    def "Deve bloquear cadastro se o candidato for menor de 18 anos"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(ICrudDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new CandidatoService(daoMock)

        and: "Um candidato com 15 anos"
        def candidato = new Candidato(
                email: "michel@teste.com",
                cpf: "12345678901",
                dataNascimento: LocalDate.now().minusYears(15) // 15 anos
        )

        when: "Tentar cadastrar no Service"
        service.cadastrar(candidato)

        then: "O sistema deve barrar lançando a RuntimeException de negócio"
        def erro = thrown(RuntimeException)
        erro.message == "O candidato deve ter pelo menos 18 anos para se cadastrar."

        and: "O banco de dados NUNCA deve ser chamado (inserir() executado 0 vezes)"
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CPF incorreto"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(ICrudDAO)

        and: "Um service recebendo a injeção pelo construtor"
        def service = new CandidatoService(daoMock)

        and: "Um candidato com CPF faltando números"
        def candidato = new Candidato(
                email: "michel@teste.com",
                cpf: "123", // Inválido
                dataNascimento: LocalDate.now().minusYears(20)
        )

        when: "Tentar cadastrar"
        service.cadastrar(candidato)

        then: "Deve barrar pelo CPF"
        def erro = thrown(RuntimeException)
        erro.message == "CPF inválido. Deve conter exatamente 11 números."
        0 * daoMock.inserir(_)
    }
}