package linketinder.service

import linketinder.dao.interfaces.IMatchDAO
import linketinder.dao.interfaces.IRecrutadorDAO
import linketinder.model.Recrutador
import spock.lang.Specification

class MatchServiceSpec extends Specification {

    def "Deve bloquear curtida de candidato se os IDs forem invalidos (zero ou negativo)"() {
        given: "Uma interface Mockada"
        def matchDaoMock = Mock(IMatchDAO)
        def recrutadorDaoMock = Mock(IRecrutadorDAO)

        and: "Um serviço recebendo injeção do construtor"
        def service = new MatchService(matchDaoMock, recrutadorDaoMock)

        when: "Tentar curtir com ID zero e ID negativo"
        service.candidatoCurteVaga(0, -5)

        then: "Deve barrar e lançar exceção"
        def erro = thrown(RuntimeException)
        erro.message == "IDs inválidos para realizar a curtida."

        and: "O banco não é chamado"
        0 * matchDaoMock.curtirVaga(_, _)
    }

    def "Deve repassar a curtida da empresa para o DAO quando IDs forem validos e retornar o Match"() {
        given: "Uma interface Mockada"
        def matchDaoMock = Mock(IMatchDAO)
        def recrutadorDaoMock = Mock(IRecrutadorDAO)

        and: "Um serviço recebendo injeção do construtor"
        def service = new MatchService(matchDaoMock, recrutadorDaoMock)

        when: "A empresa ID 10 curtir o candidato ID 5"
        boolean teveMatch = service.empresaCurteCandidato(10, 5)

        then: "O DAO deve ser acionado 1 vez retornando true, e o service confirma o Match"
        1 * matchDaoMock.curtirCandidato(10, 5) >> true
        teveMatch == true
    }

    def "Deve permitir que recrutador curta candidato repassando idRecrutador para o DAO"() {
        given: "As interfaces Mockadas"
        def matchDaoMock = Mock(IMatchDAO)
        def recrutadorDaoMock = Mock(IRecrutadorDAO)

        and: "Um recrutador vinculado à empresa ID 3"
        def recrutador = new Recrutador(id: 7, idEmpresa: 3)

        and: "Um serviço recebendo injeção do construtor com ambas as dependencias"
        def service = new MatchService(matchDaoMock, recrutadorDaoMock)

        when: "O recrutador ID 7 curte o candidato ID 5"
        boolean teveMatch = service.recrutadorCurteCandidato(7, 5)

        then: "O recrutadorDao deve buscar o recrutador uma vez"
        1 * recrutadorDaoMock.buscarPorId(7) >> recrutador

        and: "O matchDao deve ser chamado com idEmpresa, idCandidato e idRecrutador"
        1 * matchDaoMock.curtirCandidato(3, 5, 7) >> true

        and: "O match e confirmado"
        teveMatch == true
    }

    def "Deve lancar IllegalArgumentException se IDs do recrutador ou candidato forem invalidos"() {
        given: "As interfaces Mockadas"
        def matchDaoMock = Mock(IMatchDAO)
        def recrutadorDaoMock = Mock(IRecrutadorDAO)

        and: "Um serviço recebendo injeção do construtor"
        def service = new MatchService(matchDaoMock, recrutadorDaoMock)

        when: "Tentar curtir com ID de recrutador zero"
        service.recrutadorCurteCandidato(0, 5)

        then: "Deve barrar e lancar excecao"
        def erro = thrown(RuntimeException)
        erro.message == "IDs inválidos para realizar a avaliação."

        and: "Nenhum DAO e chamado"
        0 * recrutadorDaoMock.buscarPorId(_)
        0 * matchDaoMock.curtirCandidato(_, _, _)

        when: "Tentar curtir com ID de candidato negativo"
        service.recrutadorCurteCandidato(7, -3)

        then: "Deve barrar e lancar excecao novamente"
        thrown(RuntimeException)
    }

    def "Deve lancar RuntimeException se RecrutadorDAO nao foi injetado"() {
        given: "Apenas o mock do IMatchDAO (construtor antigo, sem IRecrutadorDAO)"
        def matchDaoMock = Mock(IMatchDAO)

        and: "Um serviço instanciado com o construtor de um parametro"
        def service = new MatchService(matchDaoMock)

        when: "Tentar usar o metodo de recrutador"
        service.recrutadorCurteCandidato(7, 5)

        then: "Deve lancar excecao informando que o DAO nao foi injetado"
        def erro = thrown(RuntimeException)
        erro.message == "RecrutadorDAO não foi injetado. Use o construtor com IRecrutadorDAO."

        and: "Nenhum DAO e chamado"
        0 * matchDaoMock.curtirCandidato(_, _, _)
    }
}