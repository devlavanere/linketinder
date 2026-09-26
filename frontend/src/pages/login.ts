import { StorageService } from '../services/StorageService';

const storageService = new StorageService();
const form = document.getElementById('formLogin') as HTMLFormElement;

if (form) {
    form.addEventListener('submit', (event) => {
        event.preventDefault();
        const email = (document.getElementById('emailLogin') as HTMLInputElement).value;

        // Tenta logar como candidato
        const candidato = storageService.getCandidatos().find(c => c.email === email);
        if (candidato) {
            storageService.login(candidato.id, 'candidato');
            window.location.href = '/vagas.html';
            return;
        }

        // Tenta logar como empresa
        const empresa = storageService.getEmpresas().find(e => e.email === email);
        if (empresa) {
            storageService.login(empresa.id, 'empresa');
            window.location.href = '/dashboard.html';
            return;
        }

        alert('E-mail não encontrado. Verifique ou cadastre-se primeiro!');
    });
}