package linketinder.service

import linketinder.dao.interfaces.ICrudDAO
import linketinder.model.Vaga

class VagaService {
    private ICrudDAO<Vaga, Integer> dao

    VagaService(ICrudDAO<Vaga, Integer> dao) {
        this.dao = dao
    }

    void cadastrar(Vaga v) {
        if (v.nome.length() < 5) {
            throw new RuntimeException("O título da vaga deve ter no mínimo 5 caracteres.")
        }
        dao.inserir(v)
    }

    List<Vaga> listar() {
        return dao.listarTodas()
    }

    void deletar(int idVaga) {
        boolean sucesso = dao.deletar(idVaga)
        if (!sucesso) {
            throw new RuntimeException("Nenhuma vaga encontrada com o ID numérico informado.")
        }
    }
}