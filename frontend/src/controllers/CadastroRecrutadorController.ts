import type { IRecrutadorCadastro } from '../models';
import { RegexValidator } from '../validators/RegexValidator';
import { DocumentValidator } from '../validators/DocumentValidator';
import { RecrutadorApiService } from '../services/RecrutadorApiService';

export class CadastroRecrutadorController {
    private recrutadorService: RecrutadorApiService;

    constructor(service: RecrutadorApiService) {
        this.recrutadorService = service;
    }

    iniciar() {
        const form = document.getElementById('form-recrutador') as HTMLFormElement;

        if (form) {
            form.addEventListener('submit', async (event) => {
                event.preventDefault();

                const nome = (document.getElementById('nome') as HTMLInputElement).value;
                const email = (document.getElementById('email') as HTMLInputElement).value;
                const cpf = (document.getElementById('cpf') as HTMLInputElement).value;
                const senha = (document.getElementById('senha') as HTMLInputElement).value;
                const dataNascimento = (document.getElementById('dataNascimento') as HTMLInputElement).value;
                const areaRecrutamento = (document.getElementById('areaRecrutamento') as HTMLInputElement).value;
                const descricao = (document.getElementById('descricao') as HTMLTextAreaElement).value;

                if (!RegexValidator.nome(nome)) {
                    return alert('Erro: O nome deve conter nome e sobrenome.');
                }
                if (!RegexValidator.email(email)) {
                    return alert('Erro: E-mail com formato inválido.');
                }
                if (!DocumentValidator.cpf(cpf)) {
                    return alert('Erro: CPF inválido.');
                }
                if (!this.validarSenha(senha)) {
                    return alert('Erro: Senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial.');
                }
                if (!this.validarIdade(dataNascimento)) {
                    return alert('Erro: O recrutador deve ter pelo menos 18 anos.');
                }

                const novoRecrutador: IRecrutadorCadastro = {
                    nome,
                    email,
                    cpf,
                    senha,
                    dataNascimento,
                    areaRecrutamento,
                    descricao,
                    idEmpresa: 1
                };

                try {
                    await this.recrutadorService.adicionar(novoRecrutador);
                    alert('Recrutador cadastrado com sucesso!');
                    window.location.href = '/dashboard.html';
                } catch (erro) {
                    console.error('Erro ao cadastrar recrutador:', erro);
                    alert('Erro ao cadastrar recrutador. Verifique os dados e tente novamente.');
                }
            });
        }
    }

    private validarSenha(senha: string): boolean {
        if (senha.length < 8) return false;
        if (!/[A-Z]/.test(senha)) return false;
        if (!/[a-z]/.test(senha)) return false;
        if (!/[0-9]/.test(senha)) return false;
        if (!/[^A-Za-z0-9]/.test(senha)) return false;
        return true;
    }

    private validarIdade(dataNascimento: string): boolean {
        const nascimento = new Date(dataNascimento);
        const hoje = new Date();
        let idade = hoje.getFullYear() - nascimento.getFullYear();
        const mesDiferenca = hoje.getMonth() - nascimento.getMonth();
        
        if (mesDiferenca < 0 || (mesDiferenca === 0 && hoje.getDate() < nascimento.getDate())) {
            idade--;
        }
        
        return idade >= 18;
    }
}
