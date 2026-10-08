package linketinder.service

import linketinder.dao.EmpresaDAO
import linketinder.model.Empresa

class EmpresaService {
    private EmpresaDAO dao = new EmpresaDAO()

    void cadastrar(Empresa e) {
        if (!e.email.contains("@")) {
            throw new RuntimeException("E-mail corporativo inválido.")
        }
        if (e.cnpj.length() != 14 || !e.cnpj.matches("[0-9]+")) {
            throw new RuntimeException("CNPJ inválido. Deve conter exatamente 14 números.")
        }

        dao.inserir(e)
    }

    List<Empresa> listar() {
        return dao.listarTodas()
    }

    void deletar(String cnpj) {
        boolean sucesso = dao.deletar(cnpj)
        if (!sucesso) {
            throw new RuntimeException("Nenhuma empresa encontrada com o CNPJ informado.")
        }
    }
}