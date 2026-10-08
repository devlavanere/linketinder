package linketinder.service

import linketinder.dao.CandidatoDAO
import linketinder.model.Candidato

class CandidatoService {
    private CandidatoDAO dao = new CandidatoDAO()

    void cadastrar(Candidato c) {
        // Regras de Negócio
        if (!c.email.contains("@")) {
            throw new RuntimeException("E-mail inválido. Deve conter '@'.")
        }
        if (c.cpf.length() != 11 || !c.cpf.matches("[0-9]+")) {
            throw new RuntimeException("CPF inválido. Deve conter exatamente 11 números.")
        }
        if (c.getIdade() < 18) {
            throw new RuntimeException("O candidato deve ter pelo menos 18 anos para se cadastrar.")
        }

        dao.inserir(c)
    }

    List<Candidato> listar() {
        return dao.listarTodos()
    }

    void deletar(String cpf) {
        boolean sucesso = dao.deletar(cpf)
        if (!sucesso) {
            throw new RuntimeException("Nenhum candidato encontrado com o CPF informado.")
        }
    }
}