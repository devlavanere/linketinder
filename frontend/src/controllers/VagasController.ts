import type { IVaga, ICandidato } from '../models';
import type { ICrudService } from '../services/interfaces/ICrudService';
import type { IAuthService } from '../services/interfaces/IAuthService';

export class VagasController {

    private vagaService: ICrudService<IVaga>;
    private authService: IAuthService;
    private candidatoService: ICrudService<ICandidato>;

    constructor(
        vagaService: ICrudService<IVaga>,
        authService: IAuthService,
        candidatoService: ICrudService<ICandidato>
    ) {
        this.vagaService = vagaService;
        this.authService = authService;
        this.candidatoService = candidatoService;
    }

    iniciar() {
        // 1. Proteção de Rota (Autenticação)
        const currentUser = this.authService.getCurrentUser();

        if (!currentUser || currentUser.tipo !== 'candidato') {
            alert('Acesso negado. Faça login como Candidato!');
            window.location.href = '/login.html';
            return; // Interrompe a execução
        }

        this.renderizarVagas();

        this.configurarAcoesGlobais(currentUser.id);
    }

    private renderizarVagas() {
        const vagas = this.vagaService.listar();
        const gridContainer = document.getElementById('lista-vagas');

        if (gridContainer) {
            if (vagas.length === 0) {
                gridContainer.innerHTML = '<p>Nenhuma vaga publicada no momento. Volte mais tarde!</p>';
            } else {
                gridContainer.innerHTML = '';

                vagas.forEach(vaga => {
                    const tagsHTML = vaga.competencias.map(comp => `<span class="tag">${comp}</span>`).join('');

                    const cardHTML = `
                        <div class="card-vaga" title="Detalhes da Vaga:\n${vaga.descricao}\n\nObs: A empresa será revelada apenas após o Match!">
                            <div class="empresa-anonima">🏢 Empresa Confidencial</div>
                            <h3>${vaga.titulo}</h3>
                            <div class="tags">${tagsHTML}</div>
                            
                            <button class="btn-match" onclick="alert('Interesse registrado! Se a empresa curtir você de volta, é Match!')">
                                Tenho Interesse
                            </button>
                        </div>
                    `;
                    gridContainer.innerHTML += cardHTML;
                });
            }
        }
    }

    private configurarAcoesGlobais(candidatoId: string) {
        (window as any).deletarMinhaConta = () => {
            if (confirm('Tem certeza que deseja excluir seu perfil de candidato?')) {
                // Aqui usamos o serviço do candidato para deletar!
                this.candidatoService.deletar(candidatoId);
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