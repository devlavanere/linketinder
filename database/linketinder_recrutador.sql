-- ==========================================
-- FEATURE: Recrutador
-- Script de migração: tabela recrutadores + alteração de curtidas_empresas
-- ==========================================

-- ==========================================
-- 1. CRIAÇÃO DA TABELA recrutadores
-- ==========================================

CREATE TABLE recrutadores (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    senha VARCHAR(255) NOT NULL CHECK (LENGTH(senha) >= 8),
    data_nascimento DATE NOT NULL,
    area_recrutamento VARCHAR(100),
    descricao TEXT,
    id_empresa INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    UNIQUE (email),
    UNIQUE (cpf)
);

-- Verifica unicidade global de e-mail (candidatos, empresas, recrutadores)
CREATE UNIQUE INDEX uix_recrutadores_email_global
    ON recrutadores(email)
    WHERE NOT EXISTS (
        SELECT 1 FROM candidatos WHERE candidatos.email = recrutadores.email
        UNION
        SELECT 1 FROM empresas WHERE empresas.email = recrutadores.email
    );

-- Verifica unicidade global de CPF (candidatos, recrutadores)
CREATE UNIQUE INDEX uix_recrutadores_cpf_global
    ON recrutadores(cpf)
    WHERE NOT EXISTS (
        SELECT 1 FROM candidatos WHERE candidatos.cpf = recrutadores.cpf
    );

-- ==========================================
-- 2. ALTERAÇÃO DA TABELA curtidas_empresas
-- ==========================================
-- Coluna opcional para rastreabilidade: quem fez a curtida (empresa master ou recrutador)
-- ON DELETE SET NULL garante que, ao remover o recrutador, a curtida permanece como da empresa.

ALTER TABLE curtidas_empresas
    ADD COLUMN id_recrutador INTEGER REFERENCES recrutadores(id) ON DELETE SET NULL;

-- ==========================================
-- 3. INSERÇÃO DE DADOS MOCKADOS
-- ==========================================
-- Dados fictícios para testes. NENHUM dado real é armazenado.

-- Recrutador 1: vinculado à Pastelsoft (id_empresa = 1)
INSERT INTO recrutadores (nome, email, cpf, senha, data_nascimento, area_recrutamento, descricao, id_empresa)
VALUES ('Ana Recruta', 'ana.recruta@mock.com', '999.999.999-99', 'senha1234', '1990-06-15', 'TI', 'Especialista em recrutamento de desenvolvedores backend.', 1);

-- Recrutador 2: vinculado à ZGHero Tech (id_empresa = 2)
INSERT INTO recrutadores (nome, email, cpf, senha, data_nascimento, area_recrutamento, descricao, id_empresa)
VALUES ('Pedro Talent', 'pedro.talent@mock.com', '888.888.888-88', 'senha1234', '1985-03-20', 'TI', 'Focado em talentos frontend e fullstack.', 2);

-- ==========================================
-- 4. CURTIDAS MOCKADAS COM id_recrutador
-- ==========================================
-- O recrutador da Pastelsoft (id=1) curte o candidato Carlos (id=3) em nome da empresa.
-- Isso não gera match ainda, pois Carlos não curtiu nenhuma vaga da Pastelsoft.

INSERT INTO curtidas_empresas (id_empresa, id_candidato, id_recrutador)
VALUES (1, 3, 1);

-- O recrutador da ZGHero Tech (id=2) curte o candidato Sandubinha (id=1) em nome da empresa.
-- Sandubinha curtiu a vaga da Pastelsoft (id=1), não da ZGHero, então não há match.

INSERT INTO curtidas_empresas (id_empresa, id_candidato, id_recrutador)
VALUES (2, 1, 2);

-- ==========================================
-- VERIFICAÇÃO
-- ==========================================
-- SELECT * FROM recrutadores;
-- \d curtidas_empresas
-- SELECT * FROM curtidas_empresas;
