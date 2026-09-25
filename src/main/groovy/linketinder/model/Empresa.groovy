package linketinder.model

class Empresa extends Pessoa{
    String cnpj

    void exibirPerfil() {
        println "--- PERFIL: EMPRESA ---"
        println "Empresa: $nome | CNPJ: $cnpj"
        println "E-mail Corporativo: $email"
        println "Sede: $pais (CEP: $cep)"
        println "Sobre nós: $descricao"
        println "Buscamos profissionais com as competências: ${competencias.join(', ')}"
        println "-----------------------\n"
    }
}
