package linketinder.app

import linketinder.dao.CurtidaDAO
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga
import linketinder.dao.CandidatoDAO
import linketinder.dao.EmpresaDAO
import linketinder.dao.VagaDAO

class GerenciadorDePerfis {
    private CandidatoDAO candidatoDAO = new CandidatoDAO()
    private EmpresaDAO empresaDAO = new EmpresaDAO()
    private VagaDAO vagaDAO = new VagaDAO()
    private CurtidaDAO curtidaDAO = new CurtidaDAO()

    // --- CANDIDATO ---
    void adicionarCandidato(Candidato candidato) {
        candidatoDAO.inserir(candidato)
    }

    List<Candidato> listarCandidatos() {
        return candidatoDAO.listarTodos()
    }

    void deletarCandidato(String cpf) {
        candidatoDAO.deletar(cpf)
    }

    // --- EMPRESA ---
    void adicionarEmpresa(Empresa empresa) {
        empresaDAO.inserir(empresa)
    }

    List<Empresa> listarEmpresas() {
        return empresaDAO.listarTodas()
    }

    void deletarEmpresa(String cnpj) {
        empresaDAO.deletar(cnpj)
    }

    // --- VAGA ---
    void adicionarVaga(Vaga vaga) {
        vagaDAO.inserir(vaga)
    }

    List<Vaga> listarVagas() {
        return vagaDAO.listarTodas()
    }

    void deletarVaga(int idVaga) {
        vagaDAO.deletar(idVaga)
    }

    // -- CURTIDA VAGAS / CANDIDATOS ---
    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        return curtidaDAO.curtirVaga(idCandidato, idVaga)
    }

    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        return curtidaDAO.curtirCandidato(idEmpresa, idCandidato)
    }
}