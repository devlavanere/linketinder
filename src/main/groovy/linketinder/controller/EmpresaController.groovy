package linketinder.controller

import linketinder.model.Empresa
import linketinder.service.EmpresaService
import linketinder.dao.EmpresaDAO

class EmpresaController {
    private EmpresaService empresaService

    EmpresaController() {
        this.empresaService = new EmpresaService(new EmpresaDAO())
    }

    boolean cadastrar(Empresa empresa) {
        try {
            empresaService.cadastrar(empresa)
            return true
        } catch(Exception e) {
            println "Erro ao processar o cadastro da empresa: " + e.message
            return false
        }
    }

    List<Empresa> listar() {
        return empresaService.listar()
    }

    boolean deletar(String cnpj) {
        try {
            return empresaService.deletar(cnpj)
        } catch(Exception e) {
            println "Erro ao deletar o empresa: " + e.message
            return false
        }
    }
}
