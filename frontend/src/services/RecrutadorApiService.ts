import type { IRecrutador, IRecrutadorCadastro } from '../models';

export class RecrutadorApiService {
    private readonly API_URL = 'http://localhost:8080/api/recrutadores';

    async listarPorEmpresa(idEmpresa: number): Promise<IRecrutador[]> {
        try {
            const resposta = await fetch(`${this.API_URL}?idEmpresa=${idEmpresa}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (resposta.ok) {
                return await resposta.json();
            } else {
                const erro = await resposta.json();
                throw new Error(erro.erro || 'Erro ao listar recrutadores.');
            }
        } catch (erro) {
            console.error('Falha de Comunicação ao listar recrutadores:', erro);
            throw erro;
        }
    }

    async adicionar(recrutador: IRecrutadorCadastro): Promise<void> {
        try {
            const resposta = await fetch(this.API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(recrutador)
            });

            if (resposta.status === 201) {
                const dados = await resposta.json();
                console.log('Sucesso no Backend Groovy:', dados.mensagem);
            } else {
                const erro = await resposta.json();
                throw new Error(erro.erro || 'Erro desconhecido no servidor.');
            }
        } catch (erro) {
            console.error('Falha de Comunicação com o Microsserviço (CORS ou Servidor Offline):', erro);
            throw erro;
        }
    }

    async deletar(id: number): Promise<void> {
        try {
            const resposta = await fetch(`${this.API_URL}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!resposta.ok) {
                const erro = await resposta.json();
                throw new Error(erro.erro || 'Erro ao deletar recrutador.');
            }
        } catch (erro) {
            console.error('Falha de Comunicação ao deletar recrutador:', erro);
            throw erro;
        }
    }
}
