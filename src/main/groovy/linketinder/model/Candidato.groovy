package linketinder.model

class Candidato extends Pessoa{
    String cpf
    int idade

    void exibirPerfil() {
        println "--- PERFIL: CANDIDATO ---"
        println "Nome: $nome | Idade: $idade anos"
        println "E-mail: $email | CPF: $cpf"
        println "Local: $estado (CEP: $cep)"
        println "Descrição: $descricao"
        println "Competências (Skills): ${competencias.join(', ')}"
        println "-------------------------\n"
    }
}
