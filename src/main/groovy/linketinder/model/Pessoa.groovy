package linketinder.model

abstract class Pessoa implements IPessoa {
    Integer id
    String nome
    String email
    String senha
    String pais
    String cep
    String descricao
    List<Competencia> competencias = [] // Guarda objeto Competencia
}