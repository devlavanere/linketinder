package linketinder.controller

import linketinder.dao.CurtidaDao
import linketinder.service.MatchService

class MatchController {
    private MatchService matchService

    MatchController() {
        // Injetando o DAO concreto (CurtidaDao) no Service, respeitando o contrato IMatchDAO
        this.matchService = new MatchService(new CurtidaDao())
    }

    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        try {
            return matchService.candidatoCurteVaga(idCandidato, idVaga)
        } catch (Exception e) {
            println "Erro ao processar a curtida do candidato: " + e.message
            return false
        }
    }

    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        try {
            return matchService.empresaCurteCandidato(idEmpresa, idCandidato)
        } catch (Exception e) {
            println "Erro ao processar a avaliação da empresa: " + e.message
            return false
        }
    }
}