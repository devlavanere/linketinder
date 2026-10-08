package linketinder.service

import linketinder.dao.CurtidaDAO
import spock.lang.Specification

class MatchServiceSpec extends Specification {

    def "Deve bloquear curtida de candidato se os IDs forem invalidos (zero ou negativo)"() {
        given: "Um Service e um DAO Mockado"
        def service = new MatchService()
        def daoMock = Mock(CurtidaDAO)
        service.dao = daoMock

        when: "Tentar curtir com ID zero e ID negativo"
        service.candidatoCurteVaga(0, -5)

        then: "Deve barrar e lançar exceção"
        def erro = thrown(RuntimeException)
        erro.message == "IDs inválidos para realizar a curtida."

        and: "O banco não é chamado"
        0 * daoMock.curtirVaga(_, _)
    }

    def "Deve repassar a curtida da empresa para o DAO quando IDs forem validos e retornar o Match"() {
        given: "Um Service e um DAO Mockado"
        def service = new MatchService()
        def daoMock = Mock(CurtidaDAO)
        service.dao = daoMock

        when: "A empresa ID 10 curtir o candidato ID 5"
        boolean teveMatch = service.empresaCurteCandidato(10, 5)

        then: "O DAO deve ser acionado 1 vez retornando true, e o service confirma o Match"
        1 * daoMock.curtirCandidato(10, 5) >> true
        teveMatch == true
    }
}