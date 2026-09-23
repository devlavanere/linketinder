-- 1. CRIAÇÃO DAS TABELAS (DDL)
CREATE TABLE candidatos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    sobrenome VARCHAR(50) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    pais VARCHAR(50) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    descricao TEXT,
    senha VARCHAR(255) NOT NULL CHECK (LENGTH(senha) >= 6)
);

CREATE TABLE empresas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT,
    pais VARCHAR(50) NOT NULL,
    cep VARCHAR(20) NOT NULL,
    senha VARCHAR(255) NOT NULL CHECK (LENGTH(senha) >= 6)
);

CREATE TABLE competencias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE vagas (
    id SERIAL PRIMARY KEY,
    id_empresa INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    local VARCHAR(100) NOT NULL
);

-- Tabelas para relação N:N (Muitos para Muitos)
CREATE TABLE candidato_competencia (
    id_candidato INTEGER NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    id_competencia INTEGER NOT NULL REFERENCES competencias(id) ON DELETE CASCADE,
    PRIMARY KEY (id_candidato, id_competencia)
);

CREATE TABLE vaga_competencia (
    id_vaga INTEGER NOT NULL REFERENCES vagas(id) ON DELETE CASCADE,
    id_competencia INTEGER NOT NULL REFERENCES competencias(id) ON DELETE CASCADE,
    PRIMARY KEY (id_vaga, id_competencia)
);

-- 2. INSERÇÃO DE DADOS MOCKADOS (DML)
INSERT INTO competencias (nome) VALUES 
('Python'), ('Java'), ('Groovy'), ('Angular'), ('Vue.js'), ('TypeScript');

INSERT INTO candidatos (nome, sobrenome, data_nascimento, email, cpf, pais, cep, descricao, senha) VALUES 
('Sandubinha', 'Silva', '1995-05-10', 'sandubinha@email.com', '111.111.111-11', 'Brasil', '58000-000', 'Desenvolvedor backend em busca de desafios.', 'senha123'),
('Maria', 'Souza', '1998-08-22', 'maria@email.com', '222.222.222-22', 'Brasil', '58000-001', 'Especialista em frontend apaixonada por design.', 'senha123'),
('Carlos', 'Ferreira', '1990-12-05', 'carlos@email.com', '333.333.333-33', 'Portugal', '4000-000', 'Arquiteto de software com 10 anos de experiência.', 'senha123'),
('Ana', 'Beatriz', '2001-03-15', 'ana@email.com', '444.444.444-44', 'Brasil', '58000-002', 'Dev fullstack aprendendo Groovy.', 'senha123'),
('João', 'Pedro', '1997-11-30', 'joao@email.com', '555.555.555-55', 'Brasil', '58000-003', 'Engenheiro de dados louco por Python.', 'senha123');

INSERT INTO empresas (nome, cnpj, email, descricao, pais, cep, senha) VALUES 
('Pastelsoft', '11.111.111/0001-11', 'recrutamento@pastelsoft.com', 'Especializada em ERPs para restaurantes.', 'Brasil', '01000-000', 'senha123'),
('ZGHero Tech', '22.222.222/0002-22', 'vagas@zghero.com', 'Empresa de inovação tecnológica.', 'Brasil', '02000-000', 'senha123'),
('Tech Brasil', '33.333.333/0003-33', 'rh@techbr.com', 'Consultoria de TI.', 'Brasil', '03000-000', 'senha123'),
('DevParaiba', '44.444.444/0004-44', 'contato@devpb.com', 'Fábrica de software nordestina.', 'Brasil', '58000-000', 'senha123'),
('Global Systems', '55.555.555/0005-55', 'hr@globalsystems.com', 'Multinacional de desenvolvimento.', 'Estados Unidos', '10001', 'senha123');

INSERT INTO vagas (id_empresa, nome, descricao, local) VALUES 
(1, 'Desenvolvedor Backend Pleno', 'Atuar com Spring e Java.', 'São Paulo, SP'),
(1, 'Desenvolvedor Frontend Júnior', 'Criar telas com Angular.', 'Remoto'),
(2, 'Engenheiro de Software', 'Atuar com Groovy no backend.', 'João Pessoa, PB'),
(3, 'Cientista de Dados', 'Criar modelos em Python.', 'Rio de Janeiro, RJ'),
(4, 'Dev Fullstack', 'Vaga para Vue.js e Java.', 'Remoto');

INSERT INTO candidato_competencia (id_candidato, id_competencia) VALUES 
(1, 2), (1, 3), 
(2, 4), (2, 5), (2, 6),
(3, 1), (3, 2), (3, 3),
(4, 3), (4, 6),
(5, 1);

INSERT INTO vaga_competencia (id_vaga, id_competencia) VALUES 
(1, 2), 
(2, 4), (2, 6),
(3, 3),
(4, 1),
(5, 2), (5, 5);