import type { IVaga } from "../models";
import { StorageService } from "../services/StorageService";
import Chart from 'chart.js/auto';

// Instacia o serviço e pega lista do LocalStorage
const storageService = new StorageService();
const candidatos = storageService.getCandidatos();

// Pega "div" vazia no dashboard.html
const gridContainer = document.getElementById('lista-candidatos');

// Verifica se a "div" existe
if (gridContainer) {
    // Caso a lista esteja vazia
    if (candidatos.length === 0) {
        gridContainer.innerHTML = '<p>Nenhum candidato cadastrado ainda.</p>';
    } else {
        gridContainer.innerHTML = '';

        // Percorre a lista de candidatos
        candidatos.forEach(candidato => {
            // Tranformando o array e HTML com tags
            const tagsHTML = candidato.competencias
                .map(comp => `<span class="tag">${comp}</span>`)
                .join('');
            
            // Desenhando a estrutura do card
            const cardHTML = `
                <div class="card" title="Mais informações: ${candidato.descricao}">
                    <h3>Candidato: #${candidato.id.substring(0,4)}</h3>
                    <p><strong>Idade:</strong> ${candidato.idade} anos</p>
                    <p><strong>Estado:</strong> ${candidato.estado}</p>
                    <p><strong>Descrição:</strong> ${candidato.descricao}</p>
                    <div class="tags">
                        ${tagsHTML}
                    </div>
                    <!-- Botão para atender ao requisito de Delete -->
                    <button class="btn-delete" onclick="deletarCandidato('${candidato.id}')" style="margin-top: 15px; background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                        Deletar Candidato
                    </button>
                </div>
            `;

            gridContainer.innerHTML+= cardHTML;
        } )
    }
}

// Pega tag "canvas" no dashboard.html
const ctx = document.getElementById('graficoCompetencias') as HTMLCanvasElement;

if (ctx && candidatos.length > 0) {
    // Cria objeto que recebe chave valor
    const contagem: Record<string, number> = {};

    // Contagem das competencias
    candidatos.forEach(candidato => {
        candidato.competencias.forEach(skill => {
            // Se a skill já existe no Map, soma +1. Se não, começa com 1.
            if (contagem[skill]) {
                contagem[skill]++;
            } else {
                contagem[skill] = 1;
            }
        });
    })

    // Separando o Map em duas listas (uma para os nomes, outra para os números)
    // Object.keys(contagem) devolve ['Java', 'Spring', 'Vue']
    const labels = Object.keys(contagem);
    // Object.values(contagem) devolve [2, 1, 3]
    const data = Object.values(contagem);

    // Desenhando com Chart.js
    new Chart(ctx, {
        type: 'bar', // Tipo do gráfico (barras)
        data: {
            labels: labels, // Textos do eixo X
            datasets: [{
                label: 'Quantidade de Candidatos',
                data: data, // Valores do eixo Y
                backgroundColor: 'rgba(0, 123, 255, 0.5)',
                borderColor: 'rgba(0, 123, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 } // Força o eixo Y a pular de 1 em 1
                }
            }
        }
    })
}

// Deleta candidato
(window as any).deletarCandidato = (id: string) => {
    // Janela para confirmar a exclusão
    const confirmacao = confirm('Tem certeza que deseja deletar este candidato?');
    
    if (confirmacao) {
        // Chama o método no StorageService
        storageService.deletarCandidato(id);
        
        // Recarrega a página sem o candidato deletado
        window.location.reload(); 
    }
};

// LÓGICA DE PUBLICAÇÃO DE VAGAS
const formVaga = document.getElementById('formVaga') as HTMLFormElement;

if (formVaga) {
    formVaga.addEventListener('submit', (event) => {
        event.preventDefault();
        
        const compInput = (document.getElementById('competenciasVaga') as HTMLInputElement).value;
        const arrayComp = compInput.split(',').map(c => c.trim());
        
        const novaVaga: IVaga = {
            id: Math.random().toString(36).substring(2, 9),
            // Como não tem login, a simulação usa o ID da empresa que está logada
            idEmpresa: 'empresa-anonima-123', 
            titulo: (document.getElementById('tituloVaga') as HTMLInputElement).value,
            descricao: (document.getElementById('descricaoVaga') as HTMLTextAreaElement).value,
            competencias: arrayComp
        };
        
        // Salva a vaga no banco
        storageService.adicionarVaga(novaVaga);
        
        alert('Vaga publicada com sucesso!');
        formVaga.reset(); // Limpa os campos do formulário
    });
}