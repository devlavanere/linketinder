package linketinder.model

class Vaga {
    Integer id
    Integer idEmpresa
    String nome
    String descricao
    String local
    List<Competencia> competencias = []

    void exibirVaga() {
        println "--- VAGA: $nome ---"
        println "Local: $local"
        println "Descricao: $descricao"
        println "Exige: ${competencias.join(', ')}"
        println "-------------------\n"
    }
}
