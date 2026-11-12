package linketinder.model

import java.time.LocalDate
import java.time.Period

class Recrutador {
    Integer id
    String nome
    String email
    String cpf
    String senha
    LocalDate dataNascimento
    String areaRecrutamento
    String descricao
    Integer idEmpresa

    int getIdade() {
        if (!dataNascimento) {
            throw new IllegalStateException("Impossível calcular idade: Data de nascimento não informada.")
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears()
    }

    RecrutadorDTO toDTO() {
        new RecrutadorDTO(
            id: id,
            nome: nome,
            areaRecrutamento: areaRecrutamento,
            descricao: descricao,
            idEmpresa: idEmpresa
        )
    }

    @Override
    String toString() {
        "Recrutador(id: $id, nome: $nome, email: $email, area: $areaRecrutamento, empresa: $idEmpresa)"
    }
}
