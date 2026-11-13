/*Autor: Michel Lavanere Sampaio*/

package linketinder.app

import linketinder.controller.CandidatoController
import linketinder.controller.EmpresaController
import linketinder.dao.interfaces.ICrudDAO
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService

import spock.lang.Specification

import java.time.LocalDate

class GerenciadorDePerfisSpec extends Specification {
    private injectController(GerenciadorDePerfis gerenciador, String fieldName, Object controller) {
        def field = GerenciadorDePerfis.class.getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(gerenciador, controller)
    }

    def "deve adicionar um novo candidato com sucesso"() {
        given: "um gerenciador com CandidatoService mockado via DAO e um candidato válido"
        def daoMock = Mock(ICrudDAO)
        daoMock.inserir(_) >> { Candidato c -> c.id = 1; return true }

        def candidatoController = new CandidatoController()
        def serviceField = CandidatoController.class.getDeclaredField('candidatoService')
        serviceField.setAccessible(true)
        serviceField.set(candidatoController, new CandidatoService(daoMock))

        def gerenciador = new GerenciadorDePerfis()
        injectController(gerenciador, 'candidatoController', candidatoController)

        def candidato = new Candidato(
            nome: "Teste Candidato",
            cpf: "12345678900",
            email: "teste@email.com",
            senha: "senha1234",
            dataNascimento: LocalDate.of(1999, 5, 15)
        )

        when: "o candidato é adicionado através do método"
        boolean resultado = gerenciador.adicionarCandidato(candidato)

        then: "o cadastro retorna sucesso"
        resultado == true

        and: "o DAO recebeu a chamada de inserir"
        1 * daoMock.inserir(candidato)
    }

    def "deve adicionar uma nova empresa com sucesso"() {
        given: "um gerenciador com EmpresaService mockado via DAO e uma empresa válida"
        def daoMock = Mock(ICrudDAO)
        daoMock.inserir(_) >> { Empresa e -> e.id = 1; return true }

        def empresaController = new EmpresaController()
        def serviceField = EmpresaController.class.getDeclaredField('empresaService')
        serviceField.setAccessible(true)
        serviceField.set(empresaController, new EmpresaService(daoMock))

        def gerenciador = new GerenciadorDePerfis()
        injectController(gerenciador, 'empresaController', empresaController)

        def empresa = new Empresa(
            nome: "Teste Empresa",
            cnpj: "11111111000111",
            email: "empresa@email.com",
            senha: "senha1234"
        )

        when: "a empresa é adicionada"
        boolean resultado = gerenciador.adicionarEmpresa(empresa)

        then: "o cadastro retorna sucesso"
        resultado == true

        and: "o DAO recebeu a chamada de inserir"
        1 * daoMock.inserir(empresa)
    }

    def "deve listar candidatos através do gerenciador"() {
        given: "um gerenciador com CandidatoService mockado via DAO"
        def daoMock = Mock(ICrudDAO)
        def candidatoExistente = new Candidato(id: 1, nome: "Existente", cpf: "11122233344")
        daoMock.listar() >> [candidatoExistente]

        def candidatoController = new CandidatoController()
        def serviceField = CandidatoController.class.getDeclaredField('candidatoService')
        serviceField.setAccessible(true)
        serviceField.set(candidatoController, new CandidatoService(daoMock))

        def gerenciador = new GerenciadorDePerfis()
        injectController(gerenciador, 'candidatoController', candidatoController)

        when: "a lista de candidatos é solicitada"
        def lista = gerenciador.listarCandidatos()

        then: "a lista deve conter o candidato retornado pelo DAO"
        1 * daoMock.listar() >> [candidatoExistente]
        lista != null
        lista.size() == 1
        lista[0].nome == "Existente"
    }

    def "deve listar empresas através do gerenciador"() {
        given: "um gerenciador com EmpresaService mockado via DAO"
        def daoMock = Mock(ICrudDAO)
        def empresaExistente = new Empresa(id: 1, nome: "Existente Corp", cnpj: "22233344455")
        daoMock.listar() >> [empresaExistente]

        def empresaController = new EmpresaController()
        def serviceField = EmpresaController.class.getDeclaredField('empresaService')
        serviceField.setAccessible(true)
        serviceField.set(empresaController, new EmpresaService(daoMock))

        def gerenciador = new GerenciadorDePerfis()
        injectController(gerenciador, 'empresaController', empresaController)

        when: "a lista de empresas é solicitada"
        def lista = gerenciador.listarEmpresas()

        then: "a lista deve conter a empresa retornada pelo DAO"
        1 * daoMock.listar() >> [empresaExistente]
        lista != null
        lista.size() == 1
        lista[0].nome == "Existente Corp"
    }
}

