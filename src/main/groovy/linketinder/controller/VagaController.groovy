package linketinder.controller

import linketinder.model.Vaga
import linketinder.service.VagaService

class VagaController {
    private VagaService vagaService

    VagaController() {
        this.vagaService = new VagaService()
    }

    boolean cadastrar(Vaga vaga) {
        try {
            vagaService.cadastrar(vaga)
            return true
        }catch (Exception e) {
            println "Erro ao deletar o vaga: " + e.message
            return false
        }
    }

    List<Vaga> listar() {
        return vagaService.listar()
    }

    boolean deletar(int idVaga) {
        try {
            vagaService.deletar(idVaga)
            return true
        }catch(Exception e) {
            println "Erro ao deletar a vaga: " + e.message
            return false
        }
    }
}
