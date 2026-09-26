import { StorageService } from '../services/StorageService';

const storageService = new StorageService();
const currentUser = storageService.getCurrentUser();

// PROTEÇÃO DE ROTA: Só candidato logado acessa!
if (!currentUser || currentUser.tipo !== 'candidato') {
    alert('Acesso negado. Faça login como Candidato!');
    window.location.href = '/login.html';
}

const vagas = storageService.getVagas();
const gridContainer = document.getElementById('lista-vagas');

if (gridContainer) {
    if (vagas.length === 0) {
        gridContainer.innerHTML = '<p>Nenhuma vaga publicada no momento. Volte mais tarde!</p>';
    } else {
        gridContainer.innerHTML = '';

        vagas.forEach(vaga => {
            const tagsHTML = vaga.competencias.map(comp => `<span class="tag">${comp}</span>`).join('');

            // Requisito: Anonimato + Tooltip no 'title'
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

// Funções de Sessão do Candidato
(window as any).deletarMinhaConta = () => {
    if (confirm('Tem certeza que deseja excluir seu perfil de candidato?')) {
        storageService.deletarCandidato(currentUser!.id);
        storageService.logout();
        window.location.href = '/index.html';
    }
};

(window as any).fazerLogout = () => {
    storageService.logout();
    window.location.href = '/login.html';
};