import type { IEmpresa } from "../models";
import { StorageService } from "../services/StorageService";

const storageService = new StorageService();

const form = document.getElementById('formEmpresa') as HTMLFormElement;

form.addEventListener('submit', (event) => {
    event.preventDefault();

    const competenciasInput = (document.getElementById('competencias') as HTMLInputElement).value;
    const arrayCompetencias = competenciasInput.split(',').map(tech => tech.trim());

    const novaEmpresa: IEmpresa = {
        id: Math.random().toString(36).substring(2, 9),
        nome: (document.getElementById('nome') as HTMLInputElement).value,
        email: (document.getElementById('email') as HTMLInputElement).value,
        cnpj: (document.getElementById('cnpj') as HTMLInputElement).value,
        pais: (document.getElementById('pais') as HTMLInputElement).value,
        estado: (document.getElementById('estado') as HTMLInputElement).value,
        cep: (document.getElementById('cep') as HTMLInputElement).value,
        descricao: (document.getElementById('descricao') as HTMLTextAreaElement).value,
        competencias: arrayCompetencias
    }

    storageService.adicionarEmpresa(novaEmpresa);

    alert('Candidato cadastrado com sucesso!');

    window.location.href = '/index.html';
})