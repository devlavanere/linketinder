package linketinder.service

import linketinder.dao.interfaces.IMatchDAO
import linketinder.dao.interfaces.IRecrutadorDAO
import linketinder.model.Recrutador

class MatchService {
    private IMatchDAO matchDao
    private IRecrutadorDAO recrutadorDao

    MatchService(IMatchDAO matchDao) {
        this.matchDao = matchDao
        this.recrutadorDao = null
    }

    MatchService(IMatchDAO matchDao, IRecrutadorDAO recrutadorDao) {
        this.matchDao = matchDao
        this.recrutadorDao = recrutadorDao
    }

    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        if (idCandidato <= 0 || idVaga <= 0) {
            throw new RuntimeException("IDs inválidos para realizar a curtida.")
        }
        return matchDao.curtirVaga(idCandidato, idVaga)
    }

    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        if (idEmpresa <= 0 || idCandidato <= 0) {
            throw new RuntimeException("IDs inválidos para realizar a avaliação.")
        }
        return matchDao.curtirCandidato(idEmpresa, idCandidato)
    }

    boolean recrutadorCurteCandidato(int idRecrutador, int idCandidato) {
        if (idRecrutador <= 0 || idCandidato <= 0) {
            throw new RuntimeException("IDs inválidos para realizar a avaliação.")
        }

        if (recrutadorDao == null) {
            throw new RuntimeException("RecrutadorDAO não foi injetado. Use o construtor com IRecrutadorDAO.")
        }

        Recrutador recrutador = recrutadorDao.buscarPorId(idRecrutador)
        int idEmpresa = recrutador.idEmpresa

        return matchDao.curtirCandidato(idEmpresa, idCandidato, idRecrutador)
    }
}