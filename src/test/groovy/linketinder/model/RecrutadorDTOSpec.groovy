package linketinder.model

import groovy.json.JsonOutput
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorDTOSpec extends Specification {

    def "Deve converter Recrutador para DTO contendo apenas dados públicos"() {
        given: "Um Recrutador com TODOS os dados preenchidos, incluindo sensíveis"
        def recrutador = new Recrutador(
                id: 1,
                nome: "Ana Silva",
                email: "ana@empresa.com",
                cpf: "12345678909",
                senha: "Senha@123",
                dataNascimento: LocalDate.now().minusYears(30),
                areaRecrutamento: "TI",
                descricao: "Especialista em recrutamento de TI",
                idEmpresa: 1
        )

        when: "Chama o método toDTO()"
        def dto = recrutador.toDTO()

        then: "O DTO contém os dados públicos"
        dto.id == 1
        dto.nome == "Ana Silva"
        dto.areaRecrutamento == "TI"
        dto.descricao == "Especialista em recrutamento de TI"
        dto.idEmpresa == 1

        and: "O DTO NÃO possui getters para dados sensíveis (cpf, senha, dataNascimento)"
        !dto.metaClass.hasProperty(dto, "cpf")
        !dto.metaClass.hasProperty(dto, "senha")
        !dto.metaClass.hasProperty(dto, "dataNascimento")
    }

    def "Deve serializar o DTO para JSON sem expor dados sensíveis"() {
        given: "Um Recrutador com TODOS os dados preenchidos, incluindo sensíveis"
        def recrutador = new Recrutador(
                id: 2,
                nome: "Carlos Souza",
                email: "carlos@empresa.com",
                cpf: "98765432100",
                senha: "MinhaSenha@456",
                dataNascimento: LocalDate.now().minusYears(35),
                areaRecrutamento: "RH",
                descricao: "Recrutador sênior de RH",
                idEmpresa: 2
        )

        when: "Converte para DTO e serializa para JSON"
        def dto = recrutador.toDTO()
        String json = JsonOutput.toJson(dto)

        then: "O JSON contém os dados públicos"
        json.contains('"id"')
        json.contains('"nome"')
        json.contains('"areaRecrutamento"')
        json.contains('"descricao"')
        json.contains('"idEmpresa"')

        and: "O JSON NÃO contém as palavras 'cpf' ou 'senha'"
        !json.contains("cpf")
        !json.contains("senha")
        !json.contains("dataNascimento")
    }

    def "Deve garantir que o JSON do DTO não expõe email nem CPF mesmo com valores preenchidos"() {
        given: "Um Recrutador com CPF e senha contendo padrões reconhecíveis"
        def recrutador = new Recrutador(
                id: 3,
                nome: "Maria Oliveira",
                email: "maria@corp.com",
                cpf: "11122233344",
                senha: "P@ssw0rd!",
                dataNascimento: LocalDate.now().minusYears(28),
                areaRecrutamento: "Engenharia",
                descricao: "Tech recruiter",
                idEmpresa: 3
        )

        when: "Converte para DTO e serializa para JSON"
        def dto = recrutador.toDTO()
        String json = JsonOutput.toJson(dto)

        then: "Valores sensíveis do Recrutador original não aparecem no JSON"
        !json.contains("11122233344")
        !json.contains("P@ssw0rd!")
        !json.contains("maria@corp.com")

        and: "Apenas os valores públicos estão presentes"
        json.contains("Maria Oliveira")
        json.contains("Engenharia")
        json.contains("Tech recruiter")
    }
}
