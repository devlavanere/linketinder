export interface IPessoa {
    id: string;
    nome: string;
    email: string;
    estado: string;
    cep: string;
    descricao: string;
    competencias: string[];
}

export interface ICandidato extends IPessoa {
    cpf: string;
    idade: number;
}