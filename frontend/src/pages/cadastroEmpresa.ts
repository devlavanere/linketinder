import type { IEmpresa } from "../models";
import { StorageService } from "../services/StorageService";

const storageService = new StorageService();

const form = document.getElementById('formEmpresa') as HTMLFormElement;

form.addEventListener('submit', (event) => {
    event.preventDefault();

    const competenciasInput = (document.getElementById('competencias') as HTMLInputElement).value;
    const arrayCompetencias = competenciasInput.split(',').map(tech => tech.trim());
 
})