import type { ICandidato } from '../models';
import { StorageService } from '../services/StorageService';

const storageService = new StorageService();

// Captura o formulário pelo ID no HTML - as HTMLFormElelment, garante que o elemento é um formulário
const form = document.getElementById('formCandidato') as HTMLFormElement;

// Pega evento de envio - submit
form.addEventListener('submit', (event) => {
    event.preventDefault();

    // Capturando competencias e transformando em array
    const competenciasInput = (document.getElementById('competencias') as HTMLInputElement).value;
    const arrayCompetencias = competenciasInput.split(',').map(skill => skill.trim());

    const novoCandidato: ICandidato = {
        id: Math.random().toString(36).substring(2, 9),
        nome: (document.getElementById('nome') as HTMLInputElement).value,
        email: (document.getElementById('email') as HTMLInputElement).value,
        cpf: (document.getElementById('cpf') as HTMLInputElement).value,
        idade: parseInt((document.getElementById('idade') as HTMLInputElement).value),
        estado: (document.getElementById('estado') as HTMLInputElement).value,
        cep: (document.getElementById('cep') as HTMLInputElement).value,
        descricao: (document.getElementById('descricao') as HTMLTextAreaElement).value,
        competencias: arrayCompetencias
    };

    // Chamando o serviço para adicionar candidato
    storageService.adicionarCandidato(novoCandidato);

    // Aviso candidato adicionado
    alert('Candidato cadastrado com sucesso!');

    // Redireciona para home
    window.location.href = '/index.html';
})
