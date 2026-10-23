package linketinder.controller

import linketinder.model.Candidato
import linketinder.service.CandidatoService

class CandidatoController {
    private CandidatoService candidatoService

    CandidatoController() {
        this.candidatoService = new CandidatoService()
    }

    boolean cadastrar(Candidato candidato) {
        try {
            candidatoService.cadastrar(candidato)
            return true
        } catch(Exception e) {
            println "Erro ao processar o cadastro do candidato: " + e.message
            return false
        }
    }

    List<Candidato> listar() {
        return candidatoService.listar()
    }

    boolean deletar(String cpf) {
        try {
            return candidatoService.deletar(cpf)
        } catch (Exception e) {
            println "Erro ao deletar o candidato: " + e.message
            return false
        }
    }
}
