package linketinder.model

class Empresa extends Pessoa{
    String cnpj

    String toString() {
        return "Empresa(id: $id, nome: $nome, cnpj: $cnpj, email: $email)"
    }
}
