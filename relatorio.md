# Relatório de Análise — Feature Recrutador

> **Data:** 2026-11-11  
> **Autor:** Junie (Agente de IA)  
> **Base de referência:** `AGENTS.md`, `skills/feature-com-spec/SKILL.md`, código fonte atual do Linketinder

---

## 1. Visão Geral

Este relatório apresenta uma análise crítica das regras de negócio propostas para a feature **Recrutador**. O objetivo é identificar pontos fortes, inconsistências, riscos e sugestões de melhoria antes da criação do `spec.md` e do `plano.md`.

---

## 2. Análise por Tópico

### 2.1 Dados da Entidade Recrutador

**Proposta:**
- Nome, Área de recrutamento, Descrição, E-mail, CPF, Data de nascimento, Senha.

**✅ Concordo:**
- Os atributos cobrem as necessidades mínimas de um perfil de recrutador.
- CPF e data de nascimento são coerentes com o padrão já usado pela entidade `Candidato`.
- A área de recrutamento é um diferencial útil para categorização futura.

**⚠️ Pontos de Atenção:**
- **Área de recrutamento:** Este campo não tem equivalente no modelo atual. Sugere-se criar uma tabela `areas` ou usar um campo `VARCHAR` com validação por lista fixa (TI, RH, Marketing, Segurança da Informação, etc.). Uma tabela dedicada permite normalização e evita duplicação.
- **Descrição:** O campo já existe na classe abstrata `Pessoa`. Se `Recrutador` estender `Pessoa`, este campo é herdado automaticamente. Se for uma entidade separada, precisa ser declarado explicitamente.

**💡 Sugestão:**
- Definir se `Recrutador` estende `Pessoa` ou é uma entidade independente. A escolha impacta diretamente o modelo de dados e a herança no backend.

---

### 2.2 Vínculo com a Empresa

**Proposta:** O recrutador é associado a apenas uma empresa (`idEmpresa`). O gerenciamento é feito pela conta master da Empresa.

**✅ Concordo:**
- Vínculo N:1 (Recrutador → Empresa) é a opção mais simples e alinhada com o modelo atual.
- O gerenciamento pela conta master da empresa faz sentido: a empresa é dona dos recrutadores.

**⚠️ Pontos de Atenção:**
- **Remoção do vínculo:** O que acontece com as curtidas e matches de um recrutador quando ele é desvinculado ou removido? As curtidas devem permanecer, pois foram registradas em nome da empresa (`curtidas_empresas`).
- **Migração entre empresas:** Um recrutador pode ser desvinculado de uma empresa e vinculado a outra? Se sim, o `idEmpresa` deve ser atualizável.

**💡 Sugestão:**
- Explicitar no `spec.md` o comportamento de remoção: as curtidas do recrutador **permanecem** como curtidas da empresa, pois foram registradas com `idEmpresa`, não com `idRecrutador`.

---

### 2.3 Curtidas e Match

**Proposta:** A curtida do recrutador atua em nome da Empresa. UNIQUE constraint no banco para ignorar múltiplas curtidas da mesma empresa no mesmo candidato.

**✅ Concordo:**
- Esta é a abordagem mais coerente com o modelo atual. O recrutador age como um "braço" da empresa.
- A `UNIQUE constraint` em `curtidas_empresas(id_empresa, id_candidato)` já existe no script `linketinder_match.sql` e evita duplicação naturalmente.

**⚠️ Pontos de Atenção:**
- **Rastreabilidade:** Se todas as curtidas são registradas com `idEmpresa`, perde-se a informação de **qual recrutador** curtiu o candidato. Isso pode ser relevante para auditoria ou métricas internas.

**💡 Sugestão:**
- Considerar adicionar uma coluna opcional `id_recrutador` em `curtidas_empresas` para rastreabilidade, sem alterar a lógica de match. A coluna seria `NULL` quando a curtida é feita diretamente pela empresa (não via recrutador).

---

### 2.4 Escopo de Atuação

**Proposta:** O recrutador tem acesso a **todas** as vagas da empresa, incluindo permissão para criar, editar e excluir vagas.

**✅ Concordo:**
- Acesso a todas as vagas simplifica a implementação e é consistente com o vínculo N:1.
- Permite que o recrutador gerencie o portfólio completo da empresa.

**⚠️ Pontos de Atenção:**
- **Permissões granulares:** Em empresas grandes, pode ser necessário limitar o recrutador às vagas da sua área. No entanto, isso é um requisito de segunda iteração.
- **Conflito com a Empresa master:** Se a empresa e o recrutador podem ambos criar/editar/excluir vagas, pode haver conflito. Quem tem prioridade?

**💡 Sugestão:**
- No MVP, manter o acesso total a todas as vagas. Documentar no `spec.md` que permissões granulares por área estão **fora do escopo**.

---

### 2.5 Princípio do Anonimato

**Proposta:**
- Antes do match: recrutador vê apenas competências e descrição do candidato.
- Depois do match: libera nome e e-mail para ambas as partes.
- **Restrição absoluta:** CPF, data de nascimento e senha do recrutador NUNCA são exibidos ou trafegados na API.

**✅ Concordo:**
- A aplicação do anonimato ao recrutador é **essencial** para manter a integridade do modelo de recrutamento às cegas.
- A restrição absoluta sobre CPF, data de nascimento e senha é correta e deve ser documentada como regra inegociável.

**⚠️ Pontos de Atenção:**
- **Anonimato recíproco:** O candidato vê o recrutador como "🏢 Empresa Confidencial" (mesmo comportamento que vê a empresa). Isso está correto, pois o recrutador age em nome da empresa.
- **Backend:** Os endpoints da API precisam filtrar os dados sensíveis do recrutador nas respostas GET. Isso requer uma camada de projeção ou DTO para garantir que CPF, data de nascimento e senha nunca sejam serializados.

**💡 Sugestão:**
- Criar um método específico no service ou uma projeção DTO que retorne apenas os campos seguros do recrutador (nome, área de recrutamento, descrição, e-mail) para consumo do frontend.
- Adicionar um teste Spock específico para validar que os dados sensíveis não são expostos.

---

### 2.6 Validações de Dados

**Proposta:**
- E-mail com formato válido (Regex) e único no sistema.
- CPF com 11 dígitos numéricos e único no sistema.
- Idade: recrutador deve ter mais de 18 anos.
- Senha: no mínimo 8 caracteres.

**✅ Concordo:**
- As validações são coerentes e alinhadas com as já existentes para `Candidato`.
- A validação de idade (>18 anos) é importante para conformidade legal.

**⚠️ Pontos de Atenção:**
- **Unicidade de e-mail:** O e-mail deve ser único **em todo o sistema** (não apenas entre recrutadores). Ou seja, um recrutador não pode ter o mesmo e-mail de um candidato, de uma empresa ou de outro recrutador.
- **Unicidade de CPF:** Mesma lógica — o CPF deve ser único em todo o sistema, não apenas entre recrutadores.
- **Validação de CPF:** 11 dígitos é uma validação básica. Considerar adicionar a validação dos dígitos verificadores do CPF (algoritmo módulo 11) para maior robustez.

**💡 Sugestão:**
- No banco, adicionar uma constraint `UNIQUE` no e-mail que abranja todas as tabelas de pessoas (ou usar uma trigger/check).
- No backend, reutilizar os validadores existentes (`RegexValidator`, `DocumentValidator`) se aplicável.
- A validação completa do CPF (dígitos verificadores) pode ser deixada para uma iteração futura, mas a validação de 11 dígitos deve ser implementada no MVP.

---

## 3. Plano de Ação — Análise

### 3.1 Branch `feature/recrutador`

**✅ Concordo:** Prática padrão de Git Flow.

### 3.2 Documentação em `docs/specs/recrutador/`

**✅ Concordo:** Segue o padrão definido no `SKILL.md`. A estrutura `spec.md` + `plano.md` é adequada.

### 3.3 Banco de Dados

**✅ Concordo:** Criação de tabelas e chaves estrangeiras é o primeiro passo lógico.

**💡 Sugestão:**
- O script SQL deve ser adicionado a `database/linketinder_recrutador.sql` seguindo o padrão de nomenclatura existente.
- Incluir dados mockados para testes.

### 3.4 Backend — Model, DAO, Controller, Servlet + Testes Spock

**✅ Concordo:** Segue a arquitetura MVC estrita do projeto.

**💡 Sugestão:**
- Seguir a mesma estrutura de camadas: `Recrutador` (model) → `RecrutadorDAO` (DAO) → `RecrutadorService` (service) → `RecrutadorController` (controller) → `RecrutadorServlet` (API).
- Criar `RecrutadorServiceSpec.groovy` para testes BDD.

### 3.5 Frontend — Telas, Fetch API e Controllers TypeScript

**✅ Concordo:** Integração via Fetch API é consistente com o frontend atual.

**💡 Sugestão:**
- Criar uma página de cadastro de recrutador (`cadastro-recrutador.html`) similar às existentes.
- Adicionar o recrutador ao dashboard da empresa com lista de recrutadores vinculados.

### 3.6 Validação do Princípio do Anonimato

**✅ Concordo:** Validação rigorosa é essencial e deve ser um critério de aceite explícito.

### 3.7 CHANGELOG.md e Pull Request

**✅ Concordo:** Atualização do CHANGELOG em cada tarefa segue o padrão do `SKILL.md`.

---

## 4. Riscos Identificados

| Risco | Impacto | Mitigação |
|---|---|---|
| Perda de rastreabilidade das curtidas do recrutador | Médio | Adicionar coluna `id_recrutador` opcional em `curtidas_empresas`. |
| Conflito de e-mail/CPF entre entidades diferentes | Alto | Constraint UNIQUE global ou validação no service layer. |
| Exposição acidental de dados sensíveis do recrutador | Crítico | DTO de projeção + teste Spock específico para anonimato. |
| Comportamento não definido ao remover recrutador | Médio | Documentar explicitamente no spec.md que curtidas permanecem. |

---

## 5. Resumo das Concordâncias e Discordâncias

### ✅ O que Mantenho (Concordo Totalmente)

1. **Vínculo N:1** entre recrutador e empresa — simples e coerente.
2. **Curtida do recrutador atua em nome da empresa** — preserva a lógica de match atual.
3. **Princípio do Anonimato aplicado ao recrutador** — essencial para o modelo.
4. **Restrição absoluta sobre CPF, data de nascimento e senha** — correta e necessária.
5. **Validações de dados** (e-mail, CPF, idade, senha) — alinhadas com o padrão existente.
6. **Estrutura de documentação** (`spec.md` + `plano.md` em `docs/specs/recrutador/`) — segue o `SKILL.md`.
7. **Arquitetura MVC** com testes Spock — consistente com o projeto.

### ⚠️ O que Sugiro Mudar ou Refinar

1. **Rastreabilidade das curtidas:** Adicionar coluna `id_recrutador` opcional em `curtidas_empresas` para saber quem curtiu, sem alterar a lógica de match.
2. **Unicidade global de e-mail e CPF:** Garantir que o e-mail e CPF do recrutador não colidam com candidatos ou empresas existentes.
3. **DTO de projeção:** Criar camada de projeção para garantir que dados sensíveis nunca sejam serializados na API.
4. **Comportamento de remoção:** Documentar explicitamente o que acontece com as curtidas ao remover um recrutador.
5. **Área de recrutamento:** Decidir entre campo `VARCHAR` com lista fixa ou tabela normalizada `areas`.
6. **Validação completa do CPF:** Considerar algoritmo módulo 11 em iteração futura.

### ❌ O que Discordo

- **Nenhuma discordância grave.** As regras propostas são sólidas e bem pensadas. As sugestões acima são refinamentos, não rejeições.

---

## 6. Conclusão

A feature Recrutador está bem especificada e alinhada com a arquitetura e os princípios do Linketinder. As regras de negócio são coerentes, o Princípio do Anonimato é respeitado e o plano de ação segue o `SKILL.md` corretamente.

Os principais pontos de atenção são:
1. **Rastreabilidade** das curtidas do recrutador (coluna opcional).
2. **Unicidade global** de e-mail e CPF.
3. **Proteção dos dados sensíveis** via DTO de projeção.
4. **Comportamento de remoção** do vínculo claramente documentado.

Com esses refinamentos, o `spec.md` e o `plano.md` estarão prontos para aprovação e implementação.
