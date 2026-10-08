package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.model.Vaga
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService
import linketinder.service.VagaService
import linketinder.service.MatchService

class GerenciadorDePerfis {
    private CandidatoService candidatoService = new CandidatoService()
    private EmpresaService empresaService = new EmpresaService()
    private VagaService vagaService = new VagaService()
    private MatchService matchService = new MatchService()

    // --- CANDIDATO ---
    void adicionarCandidato(Candidato candidato) { candidatoService.cadastrar(candidato) }
    List<Candidato> listarCandidatos() { return candidatoService.listar() }
    void deletarCandidato(String cpf) { candidatoService.deletar(cpf) }

    // --- EMPRESA ---
    void adicionarEmpresa(Empresa empresa) { empresaService.cadastrar(empresa) }
    List<Empresa> listarEmpresas() { return empresaService.listar() }
    void deletarEmpresa(String cnpj) { empresaService.deletar(cnpj) }

    // --- VAGA ---
    void adicionarVaga(Vaga vaga) { vagaService.cadastrar(vaga) }
    List<Vaga> listarVagas() { return vagaService.listar() }
    void deletarVaga(int idVaga) { vagaService.deletar(idVaga) }

    // -- CURTIDA VAGAS / CANDIDATOS ---
    boolean candidatoCurteVaga(int idCandidato, int idVaga) {
        return matchService.candidatoCurteVaga(idCandidato, idVaga)
    }
    boolean empresaCurteCandidato(int idEmpresa, int idCandidato) {
        return matchService.empresaCurteCandidato(idEmpresa, idCandidato)
    }
}