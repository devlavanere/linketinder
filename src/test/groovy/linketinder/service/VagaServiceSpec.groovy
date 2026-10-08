package linketinder.service

import linketinder.dao.VagaDAO
import linketinder.model.Vaga
import spock.lang.Specification

class VagaServiceSpec extends Specification {

    def "Deve bloquear cadastro se o titulo da vaga for muito curto"() {
        given: "Um Service e um DAO Mockado"
        def service = new VagaService()
        def daoMock = Mock(VagaDAO)
        service.dao = daoMock

        and: "Uma vaga com título menor que 5 caracteres"
        def vaga = new Vaga(
                nome: "Dev", // Apenas 3 letras, inválido
                descricao: "Vaga para desenvolvedor",
                local: "Remoto"
        )

        when: "Tentar cadastrar no Service"
        service.cadastrar(vaga)

        then: "O sistema deve barrar lançando a RuntimeException"
        def erro = thrown(RuntimeException)
        erro.message == "O título da vaga deve ter no mínimo 5 caracteres."

        and: "A vaga não chega no banco de dados"
        0 * daoMock.inserir(_)
    }

    def "Deve salvar vaga com sucesso quando todos os dados forem validos"() {
        given: "Um Service e um DAO Mockado"
        def service = new VagaService()
        def daoMock = Mock(VagaDAO)
        service.dao = daoMock

        and: "Uma vaga com dados perfeitos"
        def vaga = new Vaga(
                nome: "Desenvolvedor Backend Pleno",
                descricao: "Vaga para trabalhar com Groovy e Spring",
                local: "São Paulo"
        )

        when: "O serviço tentar cadastrar"
        service.cadastrar(vaga)

        then: "O DAO deve ser chamado exatamente 1 vez para inserir"
        1 * daoMock.inserir(vaga)
    }
}