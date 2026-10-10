package linketinder.dao.interfaces

interface IMatchDAO {
    boolean curtirVaga(int idCandidato, int idVaga)
    boolean curtirCandidato(int idEmpresa, int idCandidato)
}