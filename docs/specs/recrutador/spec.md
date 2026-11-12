# Especificação — Feature Recrutador

> **Projeto:** Linketinder  
> **Data:** 2026-11-12
> **Base:** `relatorio.md` (Relatório de Análise aprovado pelo Tutor), `AGENTS.md`, código fonte atual

---

## 1. Contexto e Objetivo

O Linketinder implementa o **recrutamento às cegas**: candidatos e empresas se conectam via "Match" sem revelar a identidade um do outro até o interesse mútuo. Atualmente, apenas a conta **master da Empresa** pode curtir candidatos no dashboard.

Esta feature introduz o perfil **Recrutador** — um usuário vinculado a uma empresa que age como seu "braço" no sistema. O recrutador pode curtir candidatos, criar/editar/excluir vagas e gerenciar o portfólio da empresa, **sem expor sua identidade pessoal**. O candidato continua vendo "🏢 Empresa Confidencial", pois o recrutador atua em nome da empresa.

**Objetivo:** Permitir que uma empresa crie contas de recrutador delegadas, mantendo o Princípio do Anonimato e a lógica de match inalteradas.

---

## 2. Modelo de Dados

### 2.1 Nova Entidade: Recrutador

A entidade `Recrutador` é **independente** de `Pessoa` (não estende a classe abstrata). Possui seus próprios atributos:

| Atributo | Tipo | Obrigatório | Restrição |
|---|---|---|---|
| `id` | `SERIAL` | Sim | Primary Key |
| `nome` | `VARCHAR(50)` | Sim | Nome do recrutador |
| `email` | `VARCHAR(100)` | Sim | UNIQUE global (não pode colidir com candidatos, empresas ou outros recrutadores) |
| `cpf` | `VARCHAR(14)` | Sim | UNIQUE global, 11 dígitos numéricos |
| `senha` | `VARCHAR(255)` | Sim | Mínimo 8 caracteres |
| `data_nascimento` | `DATE` | Sim | Idade mínima: 18 anos |
| `area_recrutamento` | `VARCHAR(100)` | Não | Ex: "TI", "RH", "Marketing", "Segurança da Informação" |
| `descricao` | `TEXT` | Não | Resumo profissional do recrutador |
| `id_empresa` | `INTEGER` | Sim | Foreign Key → `empresas(id)` ON DELETE CASCADE |

### 2.2 Nova Tabela: `recrutadores`

```sql
CREATE TABLE recrutadores (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL CHECK (LENGTH(senha) >= 8),
    data_nascimento DATE NOT NULL,
    area_recrutamento VARCHAR(100),
    descricao TEXT,
    id_empresa INTEGER NOT NULL REFERENCES empresas(id) ON DELETE CASCADE
);
```

### 2.3 Alteração em `curtidas_empresas`

Uma coluna opcional é adicionada à tabela existente para **rastreabilidade**:

```sql
ALTER TABLE curtidas_empresas
    ADD COLUMN id_recrutador INTEGER REFERENCES recrutadores(id) ON DELETE SET NULL;
```

- Quando `id_recrutador` é `NULL`, a curtida foi feita pela **empresa master**.
- Quando `id_recrutador` possui valor, a curtida foi feita pelo **recrutador vinculado**.
- A lógica de match **não é alterada**: a curtida continua registrada com `id_empresa`, e o algoritmo de match cruza `curtidas_candidatos` × `curtidas_empresas` via `vagas.id_empresa`.

### 2.4 Relacionamentos

```
empresas (1) ──── N ──── recrutadores
empresas (1) ──── N ──── vagas
recrutadores ──── N ──── curtidas_empresas (via id_empresa + id_recrutador opcional)
```

- **Empresa → Recrutador:** 1:N. Uma empresa pode ter vários recrutadores. Um recrutador pertence a apenas uma empresa.
- **Recrutador → Curtida:** O recrutador curte candidatos em nome da empresa. A curtida é registrada em `curtidas_empresas(id_empresa, id_candidato)` com `id_recrutador` opcional.
- **Remoção do recrutador:** As curtidas registradas com `id_recrutador` têm esta coluna definida como `NULL` (ON DELETE SET NULL), mas **permanecem** como curtidas da empresa. O match não é afetado.

---

## 3. Regras de Negócio

### 3.1 Cadastro de Recrutador

- Somente a **conta master da empresa** pode criar recrutadores vinculados a ela.
- O recrutador é criado com `id_empresa` definido automaticamente a partir da sessão da empresa logada.
- O recrutador **não pode alterar** seu próprio `id_empresa` (mudança de vínculo só via remoção e recriação).

### 3.2 Curtida do Recrutador

- Quando o recrutador curte um candidato, a curtida é registrada em `curtidas_empresas` com:
  - `id_empresa` = empresa do recrutador
  - `id_candidato` = candidato curtido
  - `id_recrutador` = ID do recrutador (para rastreabilidade)
- A curtida **atua em nome da empresa**: o algoritmo de match verifica se o candidato já curtiu alguma vaga dessa empresa.
- Se houver interesse mútuo → **Match é criado** em `matches(id_candidato, id_vaga)`.
- Se a empresa já curtiu o candidato (por outro recrutador ou pela conta master), a `UNIQUE constraint` em `(id_empresa, id_candidato)` ignora a duplicação.

### 3.3 Gerenciamento de Vagas

- O recrutador tem acesso **total** a todas as vagas da empresa vinculada: criar, editar e excluir.
- As vagas criadas pelo recrutador pertencem à empresa (`id_empresa`), não ao recrutador.
- A empresa master e o(s) recrutador(es) podem gerenciar as mesmas vagas sem conflito de prioridade.

### 3.4 Remoção do Recrutador

- Ao remover um recrutador:
  - O registro é excluído da tabela `recrutadores`.
  - As curtidas em `curtidas_empresas` **permanecem** com `id_recrutador = NULL` (ON DELETE SET NULL).
  - Os matches já criados **não são afetados**.
  - As vagas criadas pelo recrutador **permanecem** vinculadas à empresa.

### 3.5 Validações de Dados

| Campo | Regra |
|---|---|
| **E-mail** | Formato válido (Regex) + **único em todo o sistema** (não pode colidir com candidatos, empresas ou outros recrutadores) |
| **CPF** | 11 dígitos numéricos + **único em todo o sistema** (não pode colidir com candidatos) |
| **Idade** | Recrutador deve ter **mais de 18 anos** (validação por `data_nascimento`) |
| **Senha** | Mínimo **8 caracteres** |
| **id_empresa** | Deve referenciar uma empresa existente e ativa |

> **Nota:** A validação completa do CPF (algoritmo módulo 11 com dígitos verificadores) está planejada para iteração futura. No MVP, a validação de 11 dígitos é suficiente.

---

## 4. Anonimato

O Princípio do Anonimato é **regra inegociável** e se aplica integralmente ao recrutador.

### 4.1 O que o Recrutador Vê

#### Antes do Match
Ao visualizar candidatos no dashboard, o recrutador enxerga **exatamente o mesmo que a empresa master**:

| Visível | Não Visível |
|---|---|
| Idade do candidato | Nome completo |
| Lista de competências | E-mail |
| Descrição profissional (tooltip) | CPF |
| Localização (tooltip) | Data de nascimento |
| Botão de curtir | Qualquer dado sensível |

O candidato é renderizado como **"Candidato Anônimo"**.

#### Depois do Match
Após o match, o recrutador tem acesso aos dados completos do candidato:

| Liberado |
|---|
| Nome completo |
| E-mail |
| Competências |
| Descrição profissional |
| Localização |

> **Restrição:** O CPF e a data de nascimento do candidato **não são liberados** automaticamente pelo match. A revelação de dados sensíveis além de nome e e-mail é decisão futura.

### 4.2 O que o Candidato Vê

#### Antes do Match
O candidato **não distingue** se o interesse veio da empresa master ou de um recrutador. Em ambos os casos:

| Visível | Não Visível |
|---|---|
| Título da vaga | Nome da empresa |
| Descrição da vaga | CNPJ |
| Competências exigidas | E-mail da empresa |
| Botão de curtir | Nome do recrutador |

A vaga é renderizada como **"🏢 Empresa Confidencial"**.

#### Depois do Match
Após o match, o candidato descobre a identidade da empresa:

| Liberado |
|---|
| Nome da empresa |
| E-mail da empresa |
| Descrição da empresa |

> **Nota:** O nome do recrutador **não é revelado** ao candidato. O match ocorre entre candidato e vaga/empresa, não entre candidato e recrutador.

### 4.3 Restrição Absoluta — Dados Sensíveis do Recrutador

Os seguintes dados do recrutador **NUNCA** são exibidos ou trafegados na API para o frontend do candidato:

- **CPF** do recrutador
- **Data de nascimento** do recrutador
- **Senha** do recrutador

Além disso, para fins de anonimato recíproco:

- **Nome** do recrutador não é exposto ao candidato
- **E-mail** do recrutador não é exposto ao candidato

A proteção é implementada via **DTO de projeção** no backend: os endpoints GET retornam apenas os campos seguros, garantindo que dados sensíveis nunca sejam serializados.

---

## 5. Decisões Tomadas

As decisões abaixo foram **discutidas e aprovadas pelo Tutor** com base no Relatório de Análise (`relatorio.md`).

### 5.1 Vínculo: 1:N (Recrutador → Empresa)

**Decisão:** Cada recrutador é vinculado a **apenas uma empresa**. A empresa pode ter múltiplos recrutadores.

**Justificativa:** Abordagem mais simples e alinhada com o modelo atual. O vínculo N:1 evita complexidade de multi-vinculação e reflete o cenário real de recrutamento.

**Status:** ✅ Aprovado pelo Tutor.

### 5.2 Curtidas: Recrutador age em nome da Empresa

**Decisão:** A curtida do recrutador é registrada em `curtidas_empresas` com `id_empresa`. O algoritmo de match não é alterado.

**Justificativa:** O recrutador é um "braço" da empresa. Manter a curtida na tabela existente preserva toda a lógica de match atual sem refatoração.

**Status:** ✅ Aprovado pelo Tutor.

### 5.3 Rastreabilidade: Coluna `id_recrutador` opcional

**Decisão:** Adicionar coluna `id_recrutador INTEGER` (nullable) em `curtidas_empresas` para rastrear qual recrutador curtiu, sem alterar a lógica de match.

**Justificativa:** Permite auditoria e métricas internas (qual recrutador foi mais ativo) sem impactar o match. A coluna é `NULL` quando a curtida é feita pela empresa master.

**Status:** ✅ Aprovado pelo Tutor.

### 5.4 Escopo de Atuação: Acesso Global às Vagas

**Decisão:** O recrutador tem acesso **total** a todas as vagas da empresa (criar, editar, excluir).

**Justificativa:** Simplifica a implementação do MVP. Permissões granulares por área de recrutamento são complexidade de segunda iteração.

**Status:** ✅ Aprovado pelo Tutor.

### 5.5 DTO para Dados Sensíveis

**Decisão:** Criar camada de projeção (DTO) no backend para garantir que CPF, data de nascimento e senha do recrutador nunca sejam serializados na API.

**Justificativa:** Proteção contra exposição acidental de dados sensíveis. Um teste Spock específico valida que os dados sensíveis não são expostos.

**Status:** ✅ Aprovado pelo Tutor.

### 5.6 Unicidade Global de E-mail e CPF

**Decisão:** O e-mail e CPF do recrutador devem ser **únicos em todo o sistema**, não colidindo com candidatos, empresas ou outros recrutadores.

**Justificativa:** Evita conflitos de identidade entre entidades diferentes. A validação é feita no service layer, cruzando as consultas às tabelas de candidatos, empresas e recrutadores.

**Status:** ✅ Aprovado pelo Tutor.

### 5.7 Comportamento de Remoção

**Decisão:** Ao remover um recrutador, as curtidas permanecem como curtidas da empresa (`id_recrutador` vai para `NULL`). Matches e vagas não são afetados.

**Justificativa:** As curtidas foram registradas em nome da empresa, não do recrutador. Remover o recrutador não deve invalidar o trabalho já realizado.

**Status:** ✅ Aprovado pelo Tutor.

---

## 6. Impacto no Que Já Existe

### 6.1 Banco de Dados

| Alteração | Impacto |
|---|---|
| Nova tabela `recrutadores` | Sem impacto nas tabelas existentes |
| Coluna `id_recrutador` em `curtidas_empresas` | Alteração compatível: coluna nullable, default `NULL`. Registros existentes não são afetados. |
| Script `database/linketinder_recrutador.sql` | Novo script de migração, aplicado após `linketinder_match.sql` |

### 6.2 Backend

| Camada | Impacto |
|---|---|
| **Model** | Nova classe `Recrutador.groovy`. Nenhuma classe existente é modificada. |
| **DAO** | Novo `RecrutadorDAO.groovy`. `CurtidaDAO` recebe alteração para aceitar `id_recrutador` opcional em `curtirCandidato()`. |
| **Service** | Novo `RecrutadorService.groovy` com validações de unicidade global. `MatchService` recebe alteração para propagar `id_recrutador`. |
| **Controller** | Novo `RecrutadorController.groovy`. `MatchController` recebe método para curtida via recrutador. |
| **Servlet (API)** | Novo `RecrutadorServlet.groovy`. `CurtidaServlet` recebe endpoint para curtida do recrutador. |
| **DTO** | Nova classe `RecrutadorDTO.groovy` (projeção segura sem dados sensíveis). |
| **Testes Spock** | Novo `RecrutadorServiceSpec.groovy`. Atualização de `MatchServiceSpec.groovy` para cobrir curtida com `id_recrutador`. |

### 6.3 Frontend

| Arquivo | Impacto |
|---|---|
| `dashboard.html` | Nova seção para empresa gerenciar recrutadores (lista, criar, remover) |
| `cadastro-recrutador.html` | Nova página de cadastro de recrutador |
| `DashboardController.ts` | Nova funcionalidade: recrutador logado vê dashboard com candidatos (mesmo comportamento que empresa) |
| Models TypeScript | Nova interface `IRecrutador` |
| Services TypeScript | Novo serviço para CRUD de recrutadores via Fetch API |

### 6.4 Princípio do Anonimato

- **Sem impacto negativo:** O anonimato já é implementado para empresa e candidato. O recrutador herda o comportamento da empresa — o candidato continua vendo "🏢 Empresa Confidencial".
- **Novo requisito:** O backend deve filtrar dados sensíveis do recrutador nos endpoints GET, garantindo que CPF, data de nascimento e senha nunca sejam retornados.

---

## 7. Fora do Escopo

Os seguintes itens **não fazem parte** desta feature e estão planejados para iterações futuras:

- **Permissões granulares por área:** Limitar o recrutador às vagas da sua `area_recrutamento`.
- **Migração de vínculo:** Permitir que um recrutador mude de empresa sem ser removido e recriado.
- **Validação completa do CPF:** Algoritmo módulo 11 com dígitos verificadores (no MVP, valida-se apenas 11 dígitos).
- **Revelação do recrutador ao candidato:** O candidato não descobre quem foi o recrutador que curtiu seu perfil.
- **Tabela normalizada `areas`:** A `area_recrutamento` é um campo `VARCHAR` livre. Normalização via tabela dedicada é futura.
- **Login separado para recrutador:** No MVP, o recrutador opera dentro do contexto da empresa (sessão da empresa master). Autenticação independente é futura.
- **Métricas e dashboard de performance do recrutador:** A coluna `id_recrutador` permite rastreabilidade, mas as métricas e visualizações são futuras.

---

## 8. Critérios de Aceite

### 8.1 Cadastro e Vínculo

- [ ] A empresa master consegue criar um recrutador com nome, e-mail, CPF, senha, data de nascimento e área de recrutamento.
- [ ] O recrutador é vinculado automaticamente à empresa que o criou (`id_empresa`).
- [ ] O e-mail do recrutador não pode ser duplicado em todo o sistema (candidatos, empresas, outros recrutadores).
- [ ] O CPF do recrutador não pode ser duplicado em todo o sistema (candidatos e outros recrutadores).
- [ ] O recrutador deve ter mais de 18 anos no momento do cadastro.
- [ ] A senha do recrutador deve ter no mínimo 8 caracteres.
- [ ] A remoção do recrutador não afeta as curtidas, matches ou vagas já registradas.

### 8.2 Curtida e Match

- [ ] O recrutador consegue curtir candidatos no dashboard.
- [ ] A curtida do recrutador é registrada em `curtidas_empresas` com `id_empresa` correto e `id_recrutador` preenchido.
- [ ] Se a empresa já curtiu o candidato (por outro meio), a curtida duplicada é ignorada (`ON CONFLICT DO NOTHING`).
- [ ] Se o candidato já curtiu uma vaga da empresa, o match é criado corretamente.
- [ ] A curtida da empresa master (sem recrutador) continua funcionando com `id_recrutador = NULL`.

### 8.3 Gerenciamento de Vagas

- [ ] O recrutador consegue criar vagas vinculadas à sua empresa.
- [ ] O recrutador consegue editar vagas da empresa.
- [ ] O recrutador consegue excluir vagas da empresa.
- [ ] As vagas criadas pelo recrutador pertencem à empresa (`id_empresa`), não ao recrutador.

### 8.4 Princípio do Anonimato

- [ ] O candidato **nunca** vê o nome, e-mail, CPF ou data de nascimento do recrutador antes do match.
- [ ] O candidato **nunca** vê o nome do recrutador mesmo após o match (apenas a empresa é revelada).
- [ ] O recrutador vê o candidato como **"Candidato Anônimo"** antes do match (apenas idade, competências, descrição e localização).
- [ ] Após o match, o recrutador vê nome e e-mail do candidato.
- [ ] Os endpoints da API **nunca** retornam CPF, data de nascimento ou senha do recrutador (validado por DTO de projeção).
- [ ] Existe um teste Spock específico que valida que os dados sensíveis do recrutador não são expostos pela API.

### 8.5 Banco de Dados

- [ ] O script `database/linketinder_recrutador.sql` cria a tabela `recrutadores` sem erros.
- [ ] A coluna `id_recrutador` é adicionada a `curtidas_empresas` como nullable.
- [ ] Os dados mockados incluem pelo menos 2 recrutadores vinculados a empresas diferentes.
- [ ] A constraint `ON DELETE SET NULL` em `id_recrutador` funciona corretamente ao remover um recrutador.

### 8.6 Testes

- [ ] `RecrutadorServiceSpec.groovy` cobre: cadastro, validação de unicidade global, validação de idade, validação de senha.
- [ ] `MatchServiceSpec.groovy` é atualizado para cobrir curtida com `id_recrutador` preenchido e `NULL`.
- [ ] Todos os testes existentes continuam passando (regressão).
