package linketinder.app

import linketinder.model.Candidato
import linketinder.model.Empresa

class GerenciadorDePerfis {
    List<Candidato> candidatos = []
    List<Empresa> empresas = []

    void adicionarCandidato(Candidato candidato) {
        candidatos << candidato
    }

    void adicionarEmpresa(Empresa empresa) {
        empresas << empresa
    }
}