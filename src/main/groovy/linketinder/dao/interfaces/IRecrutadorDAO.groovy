package linketinder.dao.interfaces

import linketinder.model.Recrutador

interface IRecrutadorDAO extends ICrudDAO<Recrutador, Integer> {
    List<Recrutador> listarPorEmpresa(Integer idEmpresa)
    boolean emailJaEmUso(String email)
    boolean cpfJaEmUso(String cpf)
    Recrutador buscarPorId(Integer id)
}
