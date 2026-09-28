import './dashboard.css'

import type { IVaga } from "../../models";
import { StorageService } from "../../services/StorageService";
import Chart from 'chart.js/auto';

const storageService = new StorageService();
const currentUser = storageService.getCurrentUser();

// PROTEÇÃO DE ROTA: Só empresa logada acessa!
if (!currentUser || currentUser.tipo !== 'empresa') {
    alert('Acesso negado. Faça login como Empresa!');
    window.location.href = '/login.html';
}

// ----------------------------------------------------
// 1. LISTAGEM DE CANDIDATOS (ANÔNIMOS + TOOLTIP NATIVO)
// ----------------------------------------------------
const candidatos = storageService.getCandidatos();
const gridCandidatos = document.getElementById('lista-candidatos');

if (gridCandidatos) {
    if (candidatos.length === 0) {
        gridCandidatos.innerHTML = '<p>Nenhum candidato cadastrado ainda.</p>';
    } else {
        gridCandidatos.innerHTML = '';
        candidatos.forEach(candidato => {
            const tagsHTML = candidato.competencias.map(comp => `<span class="tag">${comp}</span>`).join('');
            
            // Requisito: Anonimato + Tooltip no atributo 'title'
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

// ----------------------------------------------------
// 2. LISTAGEM DAS VAGAS DA PRÓPRIA EMPRESA (COM DELETE)
// ----------------------------------------------------
const gridVagasProprias = document.getElementById('lista-vagas-proprias');
if (gridVagasProprias) {
    const minhasVagas = storageService.getVagas().filter(v => v.idEmpresa === currentUser!.id);
    
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

// ----------------------------------------------------
// 3. GRÁFICO (CHART.JS - TOOLTIP AUTOMÁTICO)
// ----------------------------------------------------
const ctx = document.getElementById('graficoCompetencias') as HTMLCanvasElement;
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

// ----------------------------------------------------
// 4. LÓGICA DE CADASTRO E DELEÇÃO (GLOBAL)
// ----------------------------------------------------
const formVaga = document.getElementById('formVaga') as HTMLFormElement;
if (formVaga) {
    formVaga.addEventListener('submit', (event) => {
        event.preventDefault();
        const arrayComp = (document.getElementById('competenciasVaga') as HTMLInputElement).value.split(',').map(c => c.trim());
        
        const novaVaga: IVaga = {
            id: Math.random().toString(36).substring(2, 9),
            idEmpresa: currentUser!.id, // Puxa o ID real da sessão!
            titulo: (document.getElementById('tituloVaga') as HTMLInputElement).value,
            descricao: (document.getElementById('descricaoVaga') as HTMLTextAreaElement).value,
            competencias: arrayComp
        };
        
        storageService.adicionarVaga(novaVaga);
        alert('Vaga publicada!');
        window.location.reload();
    });
}

(window as any).deletarMinhaVaga = (id: string) => {
    if (confirm('Tem certeza que deseja apagar esta vaga?')) {
        storageService.deletarVaga(id);
        window.location.reload();
    }
};

(window as any).deletarMinhaConta = () => {
    if (confirm('Tem certeza que deseja encerrar a conta da sua empresa? Isso apagará suas vagas.')) {
        storageService.deletarEmpresa(currentUser!.id);
        // Opcional: deletar todas as vagas dessa empresa também
        storageService.logout();
        window.location.href = '/index.html';
    }
};

(window as any).fazerLogout = () => {
    storageService.logout();
    window.location.href = '/login.html';
};