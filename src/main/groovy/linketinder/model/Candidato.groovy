package linketinder.model

import java.time.LocalDate
import java.time.Period

class Candidato extends Pessoa{
    String cpf
    String sobrenome
    LocalDate dataNascimento
    List<Competencia> competencias = []

    int getIdade() {
        if (!dataNascimento) {
            throw new IllegalStateException("Impossível calcular idade: Data de nascimento não informada.")
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears()
    }

    @Override
    String toString() {
        String skills = competencias.isEmpty() ? "Nenhuma cadastrada" : competencias*.nome.join(', ')
        return "Candidato(id: $id, nome: $nome $sobrenome, email: $email, cpf: $cpf, skills: [$skills])"
    }
}
