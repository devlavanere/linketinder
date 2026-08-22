package linketinder.model

abstract class Pessoa implements IPessoa {
    String nome
    String email
    String estado
    String cep
    String descricao
    List<String> competencias = [] // Inicializa como lita vazia
}
