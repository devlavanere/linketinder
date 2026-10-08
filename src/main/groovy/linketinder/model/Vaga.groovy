package linketinder.model

class Vaga {
    Integer id
    Integer idEmpresa
    String nome
    String descricao
    String local
    List<Competencia> competencias = []

    @Override
    String toString() {
        String exigenciasFormatadas = competencias.isEmpty() ? "Nenhuma" : competencias*.nome.join(', ')
        return "Vaga(id: $id, titulo: $nome, local: $local, exigencias: [$exigenciasFormatadas])"
    }
}
