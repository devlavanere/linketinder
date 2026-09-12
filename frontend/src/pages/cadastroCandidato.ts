import type { ICandidato } from '../models';
import { StorageService } from '../services/StorageService';

const storageService = new StorageService();

// Captura o formulário pelo ID no HTML - as HTMLFormElelment, garante que o elemento é um formulário
const form = document.getElementById('formCandidato') as HTMLFormElement;

// Pega evento de envio - submit
form.addEventListener('submit', (event) => {
    event.preventDefault();
})
