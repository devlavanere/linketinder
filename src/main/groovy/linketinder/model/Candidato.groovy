package linketinder.model

import java.time.LocalDate
import java.time.Period

class Candidato extends Pessoa{
    String cpf
    LocalDate dataNascimento

    // Método auxiliar para calcular a idade na hora de exibir
    int getIdade() {
        return dataNascimento ? Period.between(dataNascimento, LocalDate.now()).getYears() : 0
    }

    void exibirPerfil() {
        println "--- PERFIL: CANDIDATO ---"
        println "Nome: $nome | Idade: ${getIdade()}"
        println "E-mail: $email | CPF: $cpf"
        println "Local: $pais (CEP: $cep)"
        println "Descrição: $descricao"
        println "Competências (Skills): ${competencias.join(', ')}"
        println "-------------------------\n"
    }
}
