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
                <div class="card">
                    <h3>${candidato.nome}</h3>
                    <p><strong>Idade:</strong> ${candidato.idade} anos</p>
                    <p><strong>Estado:</strong> ${candidato.estado}</p>
                    <p><strong>Descrição:</strong> ${candidato.descricao}</p>
                    <div class="tags">
                        ${tagsHTML}
                    </div>
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