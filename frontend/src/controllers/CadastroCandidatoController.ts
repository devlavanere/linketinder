import type { ICandidato } from '../models';
import { RegexValidator } from '../validators/RegexValidator';
import { DocumentValidator } from '../validators/DocumentValidator';
import type { ICrudService } from '../services/interfaces/ICrudService';

export class CadastroCandidatoController {
    private candidatoService: ICrudService<ICandidato>;

    constructor(service: ICrudService<ICandidato>) {
        this.candidatoService = service;
    }

    iniciar() {
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

                if (!RegexValidator.nome(nome)) return alert('Erro: O nome deve conter nome e sobrenome.');
                if (!RegexValidator.email(email)) return alert('Erro: E-mail com formato inválido.');
                if (!DocumentValidator.cpf(cpf)) return alert('Erro: CPF inválido.');
                if (!RegexValidator.cep(cep)) return alert('Erro: O CEP deve estar no formato 00000-000.');
                if (!RegexValidator.tags(competenciasInput)) return alert('Erro: As tecnologias devem ser separadas por vírgula.');

                const arrayCompetencias = competenciasInput.split(',').map(skill => skill.trim());

                const novoCandidato: ICandidato = {
                    id: Math.random().toString(36).substring(2, 9),
                    nome, email, cpf, idade, estado, cep, descricao,
                    competencias: arrayCompetencias
                };

                this.candidatoService.adicionar(novoCandidato);

                alert('Candidato cadastrado com sucesso!');
                window.location.href = '/index.html';
            });
        }
    }
}