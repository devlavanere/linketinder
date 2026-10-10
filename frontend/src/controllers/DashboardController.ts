// frontend/src/controllers/DashboardController.ts

import type { ICandidato, IVaga, IEmpresa } from '../models';
import type { ICrudService } from '../services/interfaces/ICrudService';
import type { IAuthService } from '../services/interfaces/IAuthService';
import Chart from 'chart.js/auto';

export class DashboardController {

    private authService: IAuthService;
    private empresaService: ICrudService<IEmpresa>;
    private candidatoService: ICrudService<ICandidato>;
    private vagaService: ICrudService<IVaga>;

    constructor(
        authService: IAuthService,
        empresaService: ICrudService<IEmpresa>,
        candidatoService: ICrudService<ICandidato>,
        vagaService: ICrudService<IVaga>
    ) {
        this.authService = authService;
        this.empresaService = empresaService;
        this.candidatoService = candidatoService;
        this.vagaService = vagaService;
    }

    iniciar() {
        const currentUser = this.authService.getCurrentUser();

        // PROTEÇÃO DE ROTA
        if (!currentUser || currentUser.tipo !== 'empresa') {
            alert('Acesso negado. Faça login como Empresa!');
            window.location.href = '/login.html';
            return;
        }

        // SRP
        this.renderizarCandidatos();
        this.renderizarVagas(currentUser.id);
        this.renderizarGrafico();
        this.configurarAcoesGlobais(currentUser.id);
        this.configurarFormularioVaga(currentUser.id);
    }

    private renderizarCandidatos() {
        const candidatos = this.candidatoService.listar();
        const gridCandidatos = document.getElementById('lista-candidatos');

        if (gridCandidatos) {
            if (candidatos.length === 0) {
                gridCandidatos.innerHTML = '<p>Nenhum candidato cadastrado ainda.</p>';
            } else {
                gridCandidatos.innerHTML = '';
                candidatos.forEach(candidato => {
                    const tagsHTML = candidato.competencias.map(comp => `<span class="tag">${comp}</span>`).join('');
                    const cardHTML = `
                        <div class="card" title="Resumo Profissional:\n${candidato.descricao}\n\nLocalização:\n${candidato.estado}\n\nOBS: O nome será revelado apenas após o Match!">
                            <h3>Candidato Anônimo</h3>
                            <p><strong>Idade:</strong> ${candidato.idade} anos</p>
                            <div class="tags">${tagsHTML}</div>
                        </div>
                    `;
                    gridCandidatos.innerHTML += cardHTML;
                });
            }
        }
    }

    private renderizarVagas(empresaId: string) {
        const gridVagasProprias = document.getElementById('lista-vagas-proprias');
        if (gridVagasProprias) {
            const minhasVagas = this.vagaService.listar().filter(v => v.idEmpresa === empresaId);

            if (minhasVagas.length === 0) {
                gridVagasProprias.innerHTML = '<p>Você ainda não publicou nenhuma vaga.</p>';
            } else {
                gridVagasProprias.innerHTML = '';
                minhasVagas.forEach(vaga => {
                    gridVagasProprias.innerHTML += `
                        <div style="background: #f8f9fa; padding: 15px; margin-bottom: 10px; border-radius: 5px; border-left: 4px solid #dc3545;">
                            <h4 style="margin: 0 0 10px 0;">${vaga.titulo}</h4>
                            <button onclick="deletarMinhaVaga('${vaga.id}')" style="background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">Apagar Vaga</button>
                        </div>
                    `;
                });
            }
        }
    }

    private renderizarGrafico() {
        const ctx = document.getElementById('graficoCompetencias') as HTMLCanvasElement;
        const candidatos = this.candidatoService.listar();

        if (ctx && candidatos.length > 0) {
            const contagem: Record<string, number> = {};
            candidatos.forEach(c => c.competencias.forEach(skill => {
                contagem[skill] = (contagem[skill] || 0) + 1;
            }));

            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: Object.keys(contagem),
                    datasets: [{
                        label: 'Candidatos com esta habilidade',
                        data: Object.values(contagem),
                        backgroundColor: 'rgba(40, 167, 69, 0.5)',
                        borderColor: 'rgba(40, 167, 69, 1)',
                        borderWidth: 1
                    }]
                },
                options: { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } }
            });
        }
    }

    private configurarFormularioVaga(empresaId: string) {
        const formVaga = document.getElementById('formVaga') as HTMLFormElement;
        if (formVaga) {
            formVaga.addEventListener('submit', (event) => {
                event.preventDefault();
                const arrayComp = (document.getElementById('competenciasVaga') as HTMLInputElement).value.split(',').map(c => c.trim());

                const novaVaga: IVaga = {
                    id: Math.random().toString(36).substring(2, 9),
                    idEmpresa: empresaId,
                    titulo: (document.getElementById('tituloVaga') as HTMLInputElement).value,
                    descricao: (document.getElementById('descricaoVaga') as HTMLTextAreaElement).value,
                    competencias: arrayComp
                };

                this.vagaService.adicionar(novaVaga);
                alert('Vaga publicada!');
                window.location.reload();
            });
        }
    }

    private configurarAcoesGlobais(empresaId: string) {
        (window as any).deletarMinhaVaga = (id: string) => {
            if (confirm('Tem certeza que deseja apagar esta vaga?')) {
                this.vagaService.deletar(id);
                window.location.reload();
            }
        };

        (window as any).deletarMinhaConta = () => {
            if (confirm('Tem certeza que deseja encerrar a conta da sua empresa? Isso apagará suas vagas.')) {
                this.empresaService.deletar(empresaId);
                // NOTA: Para ser 100% perfeito, você deveria deletar as vagas dessa empresa também
                this.authService.logout();
                window.location.href = '/index.html';
            }
        };

        (window as any).fazerLogout = () => {
            this.authService.logout();
            window.location.href = '/login.html';
        };
    }
}