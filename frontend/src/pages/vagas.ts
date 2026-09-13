import { StorageService } from '../services/StorageService';

const storageService = new StorageService();
const vagas = storageService.getVagas();

const gridContainer = document.getElementById('lista-vagas');

if (gridContainer) {
    if (vagas.length === 0) {
        gridContainer.innerHTML = '<p>Nenhuma vaga publicada no momento. Volte mais tarde!</p>';
    } else {
        gridContainer.innerHTML = '';

        vagas.forEach(vaga => {
            // Transforma as competências em tags visuais
            const tagsHTML = vaga.competencias
                .map(comp => `<span class="tag">${comp}</span>`)
                .join('');

            // Construindo o HTML mantendo a empresa anônima
            const cardHTML = `
                <div class="card-vaga">
                    <div class="empresa-anonima">🏢 Empresa Confidencial</div>
                    <h3>${vaga.titulo}</h3>
                    <p><strong>Descrição:</strong> ${vaga.descricao}</p>
                    
                    <div class="tags">
                        ${tagsHTML}
                    </div>
                    
                    <button class="btn-match" onclick="alert('Interesse registrado! Se a empresa também curtir seu perfil, acontecerá um Match!')">
                        Tenho Interesse
                    </button>
                </div>
            `;
            
            gridContainer.innerHTML += cardHTML;
        });
    }
}