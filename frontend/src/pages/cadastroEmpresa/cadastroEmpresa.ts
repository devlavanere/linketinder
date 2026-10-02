import './cadastro-empresa.css'

import type { IEmpresa } from "../../models";
import { StorageService } from "../../services/StorageService";
import { RegexValidators } from '../../validators/validators';

const storageService = new StorageService();
const form = document.getElementById('formEmpresa') as HTMLFormElement;

if (form) {
    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const nome = (document.getElementById('nome') as HTMLInputElement).value;
        const email = (document.getElementById('email') as HTMLInputElement).value;
        const cnpj = (document.getElementById('cnpj') as HTMLInputElement).value;
        const pais = (document.getElementById('pais') as HTMLInputElement).value;
        const estado = (document.getElementById('estado') as HTMLInputElement).value;
        const cep = (document.getElementById('cep') as HTMLInputElement).value;
        const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value;
        const competenciasInput = (document.getElementById('competencias') as HTMLInputElement).value;

        // Validações
        if (nome.trim().length < 3) {
            return alert('Erro: O nome da empresa precisa ter no mínimo 3 caracteres.');
        }
        if (!RegexValidators.email(email)) {
            return alert('Erro: E-mail com formato inválido (ex: empresa@dominio.com).');
        }
        if (!RegexValidators.cnpj(cnpj)) {
            return alert('Erro: CNPJ inválido ou matemático incorreto.');
        }
        if (!RegexValidators.cep(cep)) {
            return alert('Erro: O CEP deve estar no formato 00000-000.');
        }
        if (!RegexValidators.tags(competenciasInput)) return alert('Erro: As tecnologias devem ser separadas por vírgula.');

        const arrayCompetencias = competenciasInput.split(',').map(tech => tech.trim());

        const novaEmpresa: IEmpresa = {
            id: Math.random().toString(36).substring(2, 9),
            nome,
            email,
            cnpj,
            pais,
            estado,
            cep,
            descricao,
            competencias: arrayCompetencias
        };

        storageService.adicionarEmpresa(novaEmpresa);
        alert('Empresa cadastrada com sucesso!');
        window.location.href = '/index.html';
    });
}