import './cadastro-candidato.css'

import type { ICandidato } from '../../models';
import { StorageService } from '../../services/StorageService';
import { RegexValidators } from '../../validators/validators';

const storageService = new StorageService();
const form = document.getElementById('formCandidato') as HTMLFormElement;

if (form) {
    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const nome = (document.getElementById('nome') as HTMLInputElement).value;
        const email = (document.getElementById('email') as HTMLInputElement).value;
        const cpf = (document.getElementById('cpf') as HTMLInputElement).value;
        const idade = parseInt((document.getElementById('idade') as HTMLInputElement).value);
        const estado = (document.getElementById('estado') as HTMLInputElement).value;
        const cep = (document.getElementById('cep') as HTMLInputElement).value;
        const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value;
        const competenciasInput = (document.getElementById('competencias') as HTMLInputElement).value;

        // Validações
        if (!RegexValidators.nome(nome)) {
            return alert('Erro: O nome deve conter nome e sobrenome (apenas letras).');
        }
        if (!RegexValidators.email(email)) {
            return alert('Erro: E-mail com formato inválido (ex: usuario@dominio.com).');
        }
        if (!RegexValidators.cpf(cpf)) {
            return alert('Erro: CPF inválido ou matemático incorreto.');
        }
        if (!RegexValidators.cep(cep)) {
            return alert('Erro: O CEP deve estar no formato 00000-000.');
        }
        if (!RegexValidators.tags(competenciasInput)) return alert('Erro: As tecnologias devem ser separadas por vírgula.');

        // Passou em tudo! Monta e salva.
        const arrayCompetencias = competenciasInput.split(',').map(skill => skill.trim());

        const novoCandidato: ICandidato = {
            id: Math.random().toString(36).substring(2, 9),
            nome,
            email,
            cpf,
            idade,
            estado,
            cep,
            descricao,
            competencias: arrayCompetencias
        };

        storageService.adicionarCandidato(novoCandidato);
        alert('Candidato cadastrado com sucesso!');
        window.location.href = '/index.html'; // Redirecionamento reativado!
    });
}