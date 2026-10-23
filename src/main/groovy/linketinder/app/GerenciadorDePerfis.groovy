package linketinder.app

import linketinder.controller.CandidatoController
import linketinder.controller.EmpresaController
import linketinder.controller.MatchController
import linketinder.controller.VagaController
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga

class GerenciadorDePerfis {
    private CandidatoController candidatoController = new CandidatoController()
    private EmpresaController empresaController = new EmpresaController()
    private VagaController vagaController = new VagaController()
    private MatchController matchController = new MatchController()

    boolean adicionarCandidato(Candidato candidato) {
        return candidatoController.cadastrar(candidato)
    }

    List<Candidato> listarCandidatos() {
        return candidatoController.listar()
    }

    boolean deletarCandidato(String cpf) {
        return candidatoController.deletar(cpf)
    }

    boolean adicionarEmpresa(Empresa empresa) {
        return empresaController.cadastrar(empresa)
    }

    List<Empresa> listarEmpresas() {
        return empresaController.listar()
    }

    boolean deletarEmpresa(String cnpj) {
        return empresaController.deletar(cnpj)
    }

    boolean adicionarVaga(Vaga vaga) {
       return vagaController.cadastrar(vaga)
    }

    List<Vaga> listarVagas() {
        return vagaController.listar()
    }

    boolean deletarVaga(int idVaga) {
        return vagaController.deletar(idVaga)
    }

    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        return matchController.candidatoCurteVaga(idCandidato, idVaga)
    }

    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        return matchController.empresaCurteCandidato(idEmpresa, idCandidato)
    }
}