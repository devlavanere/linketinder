package linketinder.model

class RecrutadorDTO {
    Integer id
    String nome
    String areaRecrutamento
    String descricao
    Integer idEmpresa

    @Override
    String toString() {
        "RecrutadorDTO(id: $id, nome: $nome, area: $areaRecrutamento, empresa: $idEmpresa)"
    }
}
