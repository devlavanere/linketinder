package linketinder.service

import linketinder.dao.interfaces.IRecrutadorDAO
import linketinder.model.Recrutador
import spock.lang.Specification
import java.time.LocalDate

class RecrutadorServiceSpec extends Specification {

    def "Deve cadastrar recrutador com dados válidos"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com dados válidos"
        def recrutador = new Recrutador(
                nome: "Ana Silva",
                email: "ana@empresa.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Especialista em recrutamento de TI",
                idEmpresa: 1
        )

        when: "Cadastrar no Service"
        service.cadastrar(recrutador)

        then: "O DAO deve ser chamado exatamente 1 vez"
        1 * daoMock.inserir(_)

        and: "Unicidade deve ser verificada e retornar falso (não duplicado)"
        1 * daoMock.emailJaEmUso("ana@empresa.com") >> false
        1 * daoMock.cpfJaEmUso("12345678909") >> false
    }

    def "Deve bloquear cadastro se o recrutador for menor de 18 anos"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com 15 anos"
        def recrutador = new Recrutador(
                nome: "Menor",
                email: "menor@teste.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(15),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar no Service"
        service.cadastrar(recrutador)

        then: "O sistema deve barrar lançando IllegalArgumentException"
        def erro = thrown(IllegalArgumentException)
        erro.message == "O recrutador deve ter pelo menos 18 anos para se cadastrar."

        and: "O banco de dados NUNCA deve ser chamado"
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CPF com menos de 11 dígitos"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com CPF curto"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "123",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelo CPF"
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF inválido. Deve conter exatamente 11 dígitos."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CPF com todos os dígitos iguais"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com CPF repetido"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "11111111111",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelo CPF repetido"
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF inválido. CPFs com todos os dígitos iguais não são aceitos."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CPF onde dígitos verificadores não conferem"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com CPF de 11 dígitos mas inválido pelo Módulo 11"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678901",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelos dígitos verificadores"
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF inválido. Dígitos verificadores não conferem."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com e-mail sem '@'"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com e-mail inválido"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "sem-arrobo",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelo e-mail"
        def erro = thrown(IllegalArgumentException)
        erro.message.contains("E-mail inválido")
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com data de nascimento nula"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com data de nascimento nula"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: null,
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pela data nula"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Data de nascimento não pode ser nula."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com data de nascimento no futuro"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com data de nascimento no futuro"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().plusYears(1),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pela data futura"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Data de nascimento não pode ser uma data futura."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com senha menor que 8 caracteres"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com senha curta"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678909",
                senha: "Ab1!",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pela senha curta"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Senha deve ter pelo menos 8 caracteres."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com senha sem letra maiúscula"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com senha sem maiúscula"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678909",
                senha: "senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pela falta de maiúscula"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Senha deve conter pelo menos uma letra maiúscula."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com senha sem número"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com senha sem número"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@teste.com",
                cpf: "12345678909",
                senha: "Senha@abc",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pela falta de número"
        def erro = thrown(IllegalArgumentException)
        erro.message == "Senha deve conter pelo menos um número."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com e-mail duplicado"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com e-mail já em uso"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "duplicado@teste.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        and: "O mock retorna true para e-mail já em uso"
        1 * daoMock.emailJaEmUso("duplicado@teste.com") >> true

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelo e-mail duplicado"
        def erro = thrown(IllegalArgumentException)
        erro.message == "E-mail já está em uso por outro candidato, empresa ou recrutador."
        0 * daoMock.inserir(_)
    }

    def "Deve bloquear cadastro com CPF duplicado"() {
        given: "Uma interface Mockada de CRUD"
        def daoMock = Mock(IRecrutadorDAO)

        and: "Um Service recebendo a injeção pelo construtor"
        def service = new RecrutadorService(daoMock)

        and: "Um recrutador com CPF já em uso"
        def recrutador = new Recrutador(
                nome: "Teste",
                email: "teste@unicao.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Teste",
                idEmpresa: 1
        )

        and: "O mock retorna false para e-mail (ok) mas true para CPF duplicado"
        1 * daoMock.emailJaEmUso("teste@unicao.com") >> false
        1 * daoMock.cpfJaEmUso("12345678909") >> true

        when: "Tentar cadastrar"
        service.cadastrar(recrutador)

        then: "Deve barrar pelo CPF duplicado"
        def erro = thrown(IllegalArgumentException)
        erro.message == "CPF já está em uso por outro candidato ou recrutador."
        0 * daoMock.inserir(_)
    }
}
