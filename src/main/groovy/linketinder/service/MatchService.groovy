package linketinder.service

import linketinder.dao.CurtidaDAO

class MatchService {
    private CurtidaDAO dao = new CurtidaDAO()

    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        if (idCandidato <= 0 || idVaga <= 0) {
            throw new RuntimeException("IDs inválidos para realizar a curtida.")
        }
        return dao.curtirVaga(idCandidato, idVaga)
    }

    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        if (idEmpresa <= 0 || idCandidato <= 0) {
            throw new RuntimeException("IDs inválidos para realizar a avaliação.")
        }
        return dao.curtirCandidato(idEmpresa, idCandidato)
    }
}