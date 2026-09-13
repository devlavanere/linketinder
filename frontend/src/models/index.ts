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

export interface IEmpresa extends IPessoa {
    cnpj: string;
    pais: string;
}

export interface IVaga {
    id: string;
    idEmpresa: string;
    titulo: string;
    descricao: string;
    competencias: string[];
}