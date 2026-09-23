-- ==========================================
-- 1. CRIAÇÃO DAS TABELAS DE CURTIDAS E MATCH
-- ==========================================

CREATE TABLE curtidas_candidatos (
    id_candidato INTEGER NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    id_vaga INTEGER NOT NULL REFERENCES vagas(id) ON DELETE CASCADE,
    PRIMARY KEY (id_candidato, id_vaga)
);

CREATE TABLE curtidas_empresas (
    id_empresa INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE,
    id_candidato INTEGER NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    PRIMARY KEY (id_empresa, id_candidato)
);

CREATE TABLE matches (
    id SERIAL PRIMARY KEY,
    id_candidato INTEGER NOT NULL REFERENCES candidatos(id) ON DELETE CASCADE,
    id_vaga INTEGER NOT NULL REFERENCES vagas(id) ON DELETE CASCADE,
    UNIQUE (id_candidato, id_vaga)
);

-- ==========================================
-- 2. INSERÇÃO DE CURTIDAS (SIMULANDO O CASO 1)
-- ==========================================

-- Sandubinha (id=1) curte a Vaga de Desenvolvedor Backend da Pastelsoft (id=1)
INSERT INTO curtidas_candidatos (id_candidato, id_vaga) VALUES (1, 1);

-- Maria (id=2) curte a Vaga Frontend da Pastelsoft (id=2)
INSERT INTO curtidas_candidatos (id_candidato, id_vaga) VALUES (2, 2);

-- Tech Recruiter da Pastelsoft (id_empresa=1) curte o perfil do Sandubinha (id=1)
INSERT INTO curtidas_empresas (id_empresa, id_candidato) VALUES (1, 1);

-- ZGHero (id_empresa=2) curte a Maria (id=2), mas ela curtiu a vaga da Pastelsoft, então não há match!
INSERT INTO curtidas_empresas (id_empresa, id_candidato) VALUES (2, 2);


-- ==========================================
-- 3. O ALGORITMO DE MATCH EM SQL (O GATILHO)
-- ==========================================
-- Essa Query insere automaticamente na tabela 'matches' apenas se houver interesse mútuo.

INSERT INTO matches (id_candidato, id_vaga)
SELECT 
    cc.id_candidato, 
    cc.id_vaga
FROM 
    curtidas_candidatos cc
JOIN 
    vagas v ON cc.id_vaga = v.id
JOIN 
    curtidas_empresas ce ON ce.id_empresa = v.id_empresa AND ce.id_candidato = cc.id_candidato
ON CONFLICT (id_candidato, id_vaga) DO NOTHING;

-- testa para ver o Match gerado:
-- SELECT * FROM matches;