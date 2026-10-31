import type { ICandidato } from '../models';
import type { ICrudService } from './interfaces/ICrudService';

export class CandidatoApiService implements ICrudService<ICandidato> {
    private readonly API_URL = 'http://localhost:8080/api/candidatos';

    listar(): ICandidato[] {
        // Para este desafio exploratório, o listar está fora do escopo (ficará vazio)
        console.warn("Listar via API ainda não implementado no Frontend");
        return [];
    }

    async adicionar(candidato: ICandidato): Promise<void> {

        const cpfLimpo = candidato.cpf.replace(/\D/g, '');
        const cepLimpo = candidato.cep.replace(/\D/g, '');

        const anoAtual = new Date().getFullYear();
        const anoNascimento = anoAtual - candidato.idade;

        const dataNascimentoMock = `${anoNascimento}-01-01`;

        const payloadGroovy = {
            nome: candidato.nome.split(' ')[0], // Groovy espera 'nome' e 'sobrenome' separados
            sobrenome: candidato.nome.substring(candidato.nome.indexOf(' ') + 1),
            email: candidato.email,
            cpf: cpfLimpo,
            senha: "senha_padrao_frontend", // O TS não coleta senha, mandamos um mock
            pais: candidato.estado,         // Usando o estado no lugar do país provisoriamente
            cep: cepLimpo,
            descricao: candidato.descricao,
            competencias: candidato.competencias,
            // A data o Groovy espera YYYY-MM-DD. Como o HTML só tem idade, foi passado nulo
            dataNascimento: dataNascimentoMock
        };

        try {
            console.log("Disparando Fetch para o Servidor Groovy...");
            const resposta = await fetch(this.API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payloadGroovy)
            });

            if (resposta.status === 201) {
                const dados = await resposta.json();
                console.log("Sucesso no Backend Groovy:", dados.mensagem);
            } else {
                const erro = await resposta.json();
                throw new Error(erro.erro || "Erro desconhecido no servidor.");
            }
        } catch (erro) {
            console.error("Falha de Comunicação com o Microsserviço (CORS ou Servidor Offline):", erro);
            throw erro;
        }
    }

    deletar(id: string): void {
        console.warn(`Deletar o ID ${id} via API ainda não implementado no Frontend`);
    }
}