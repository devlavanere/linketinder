package linketinder.controller

import linketinder.model.Recrutador
import linketinder.model.RecrutadorDTO
import linketinder.service.RecrutadorService
import linketinder.dao.RecrutadorDAO

class RecrutadorController {
    private RecrutadorService recrutadorService

    RecrutadorController() {
        this.recrutadorService = new RecrutadorService(new RecrutadorDAO())
    }

    boolean cadastrar(Recrutador recrutador) {
        try {
            recrutadorService.cadastrar(recrutador)
            return true
        } catch (Exception e) {
            println "Erro ao processar o cadastro do recrutador: " + e.message
            return false
        }
    }

    /**
     * Lista recrutadores de uma empresa, retornando DTOs sem dados sensíveis.
     * Garante o Princípio do Anonimato: cpf, senha e dataNascimento nunca são expostos.
     */
    List<RecrutadorDTO> listarPorEmpresa(Integer idEmpresa) {
        List<Recrutador> recrutadores = recrutadorService.listarPorEmpresa(idEmpresa)
        recrutadores.collect { it.toDTO() }
    }

    boolean deletar(Integer id) {
        try {
            recrutadorService.deletar(id)
            return true
        } catch (Exception e) {
            println "Erro ao deletar o recrutador: " + e.message
            return false
        }
    }
}
