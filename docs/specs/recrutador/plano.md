# Plano de Implementação — Feature Recrutador

> **Projeto:** Linketinder  
> **Data:** 2026-11-12  
> **Base:** `docs/specs/recrutador/spec.md`

---

## Visão Geral

O plano de implementação da feature Recrutador está dividido em **13 tarefas incrementais**, organizadas em três fases:

1. **Banco de Dados** (1 tarefa) — Script SQL com tabela, alteração e dados mockados.
2. **Backend com testes Spock** (9 tarefas) — Model, DTO, DAO, Service, Controller, Servlet, alteração do CurtidaDAO/MatchService e testes.
3. **Frontend** (3 tarefas) — Interface TypeScript, serviço API, página de cadastro e integração ao dashboard.

Cada tarefa indica: o que será feito, camada afetada, arquivos envolvidos e como validar.

---

## Tarefas — Banco de Dados

### Tarefa 1 — Script SQL: tabela `recrutadores`, alteração de `curtidas_empresas` e dados mockados

- **O que será feito:** Criar o script de migração SQL com a nova tabela `recrutadores`, a coluna `id_recrutador` em `curtidas_empresas` e dados mockados.
- **Camada:** DB
- **Arquivos:** `database/linketinder_recrutador.sql` (novo)
- **Como validar:** Executar `psql -d linketinder -f database/linketinder_recrutador.sql` e verificar:
  - `\dt recrutadores` — tabela existe com todas as colunas.
  - `SELECT * FROM recrutadores;` — 2 recrutadores mockados vinculados a empresas diferentes.
  - `\d curtidas_empresas` — coluna `id_recrutador` presente como nullable com FK e ON DELETE SET NULL.
  - Inserir uma curtida com `id_recrutador` e remover o recrutador: verificar que `id_recrutador` vai para NULL mas a curtida permanece.

---

## Tarefas — Backend com Testes Spock

### Tarefa 2 — Model `Recrutador` e DTO de projeção

- **O que será feito:** Criar a classe `Recrutador.groovy` (model independente, não estende Pessoa) e o `RecrutadorDTO.groovy` (projeção segura sem dados sensíveis).
- **Camada:** Backend (Model)
- **Arquivos:**
  - `src/main/groovy/linketinder/model/Recrutador.groovy` (novo)
  - `src/main/groovy/linketinder/model/RecrutadorDTO.groovy` (novo)
- **Detalhes:**
  - `Recrutador.groovy` com atributos: `id`, `nome`, `email`, `cpf`, `senha`, `dataNascimento`, `areaRecrutamento`, `descricao`, `idEmpresa`.
  - Implementar método `getIdade()` similar ao `Candidato` (usando `Period.between`).
  - `RecrutadorDTO.groovy` com apenas: `id`, `nome`, `areaRecrutamento`, `descricao`, `idEmpresa` (sem `cpf`, `senha`, `dataNascimento`).
  - Implementar método `toDTO()` em `Recrutador` para conversão segura.
- **Como validar:** Compilar com `./gradlew build` e verificar que o DTO não expõe campos sensíveis.

### Tarefa 3 — DAO do Recrutador com unicidade global

- **O que será feito:** Criar `RecrutadorDAO.groovy` com CRUD completo e consultas de unicidade global (e-mail e CPF cruzando candidatos, empresas e recrutadores).
- **Camada:** Backend (DAO)
- **Arquivos:**
  - `src/main/groovy/linketinder/dao/RecrutadorDAO.groovy` (novo)
- **Detalhes:**
  - Implementar `inserir(Recrutador)` com `RETURN_GENERATED_KEYS`.
  - Implementar `listar()` e `listarPorEmpresa(idEmpresa)`.
  - Implementar `deletar(id)`.
  - Implementar `emailJaEmUso(email)` consultando `candidatos`, `empresas` e `recrutadores`.
  - Implementar `cpfJaEmUso(cpf)` consultando `candidatos` e `recrutadores`.
- **Como validar:** Compilar com `./gradlew build` e testar manualmente no banco após inserção via DAO.

### Tarefa 4 — Service do Recrutador com validações de negócio

- **O que será feito:** Criar `RecrutadorService.groovy` com todas as regras de negócio: validação de e-mail, CPF, idade, senha e unicidade global.
- **Camada:** Backend (Service)
- **Arquivos:**
  - `src/main/groovy/linketinder/service/RecrutadorService.groovy` (novo)
- **Detalhes:**
  - Injetar `RecrutadorDAO` via construtor.
  - Implementar `cadastrar(Recrutador)` com validações:
    - E-mail contém '@'.
    - CPF 11 dígitos.
    - Idade >18 anos.
    - Senha ≥8 caracteres.
    - Unicidade global de e-mail e CPF.
  - Implementar `listar()`, `listarPorEmpresa(idEmpresa)`, `deletar(id)`.
- **Como validar:** Compilar com `./gradlew build` e verificar que `RuntimeException` é lançada corretamente em cada caso de falha.

### Tarefa 5 — Testes Spock do `RecrutadorService`

- **O que será feito:** Criar `RecrutadorServiceSpec.groovy` com testes BDD cobrindo todas as validações de negócio.
- **Camada:** Backend (Testes)
- **Arquivos:**
  - `src/test/groovy/linketinder/service/RecrutadorServiceSpec.groovy` (novo)
- **Detalhes:**
  - Teste: bloquear cadastro se menor de 18 anos (similar a `CandidatoServiceSpec`).
  - Teste: bloquear cadastro com CPF inválido (menos de 11 dígitos).
  - Teste: bloquear cadastro com senha curta (menos de 8 caracteres).
  - Teste: bloquear cadastro com e-mail duplicado (unicidade global).
  - Teste: bloquear cadastro com CPF duplicado (unicidade global).
  - Teste: cadastro bem-sucedido com dados válidos.
- **Como validar:** Executar `./gradlew test --tests "linketinder.service.RecrutadorServiceSpec"` e verificar todos os specs passando.

### Tarefa 6 — Alterar `CurtidaDAO` para suportar `id_recrutador`

- **O que será feito:** Atualizar `CurtidaDAO.groovy` e `IMatchDAO.groovy` para aceitar `id_recrutador` opcional na curtida da empresa.
- **Camada:** Backend (DAO)
- **Arquivos:**
  - `src/main/groovy/linketinder/dao/interfaces/IMatchDAO.groovy` (alteração)
  - `src/main/groovy/linketinder/dao/CurtidaDAO.groovy` (alteração)
- **Detalhes:**
  - Adicionar método `curtirCandidato(int idEmpresa, int idCandidato, Integer idRecrutador)` em `IMatchDAO`.
  - Atualizar `inserirCurtidaEmpresa()` em `CurtidaDAO` para incluir `id_recrutador` no INSERT.
  - Manter compatibilidade: o método antigo `curtirCandidato(int, int)` delega ao novo com `idRecrutador = null`.
- **Como validar:** Compilar com `./gradlew build` e verificar que a coluna `id_recrutador` é populada corretamente no banco.

### Tarefa 7 — Alterar `MatchService` para propagar `id_recrutador`

- **O que será feito:** Atualizar `MatchService.groovy` para expor método de curtida via recrutador.
- **Camada:** Backend (Service)
- **Arquivos:**
  - `src/main/groovy/linketinder/service/MatchService.groovy` (alteração)
- **Detalhes:**
  - Adicionar método `recrutadorCurteCandidato(int idRecrutador, int idCandidato)` com validação de IDs.
  - O método deve primeiro buscar o `idEmpresa` do recrutador (via um novo método no `RecrutadorDAO` ou passado como parâmetro).
  - Delegar para `dao.curtirCandidato(idEmpresa, idCandidato, idRecrutador)`.
- **Como validar:** Compilar com `./gradlew build` e verificar que o fluxo de match funciona com `id_recrutador` preenchido.

### Tarefa 8 — Atualizar testes Spock do `MatchService`

- **O que será feito:** Atualizar `MatchServiceSpec.groovy` para cobrir curtida com `id_recrutador` preenchido e NULL.
- **Camada:** Backend (Testes)
- **Arquivos:**
  - `src/test/groovy/linketinder/service/MatchServiceSpec.groovy` (alteração)
- **Detalhes:**
  - Teste: recrutador curte candidato com `id_recrutador` preenchido, DAO é chamado com 3 parâmetros.
  - Teste: empresa master curte candidato com `id_recrutador` NULL, DAO é chamado com null.
  - Teste: bloquear curtida do recrutador se IDs forem inválidos.
- **Como validar:** Executar `./gradlew test --tests "linketinder.service.MatchServiceSpec"` e verificar regressão.

### Tarefa 9 — Controller e Servlet do Recrutador

- **O que será feito:** Criar `RecrutadorController.groovy` e `RecrutadorServlet.groovy` para expor endpoints REST do CRUD de recrutadores.
- **Camada:** Backend (Controller + API)
- **Arquivos:**
  - `src/main/groovy/linketinder/controller/RecrutadorController.groovy` (novo)
  - `src/main/groovy/linketinder/api/RecrutadorServlet.groovy` (novo)
- **Detalhes:**
  - `RecrutadorController` com métodos: `cadastrar`, `listarPorEmpresa`, `deletar`.
  - `RecrutadorServlet` mapeado em `/api/recrutadores` com CORS headers.
  - POST: cadastrar recrutador, parse JSON, retornar 201/400.
  - GET: listar recrutadores de uma empresa, retornar **DTOs** — nunca dados sensíveis.
  - DELETE: remover recrutador por ID.
- **Como validar:** Iniciar servidor com `./gradlew appRun` e testar os endpoints via curl ou navegador.
- **Verificação do Anonimato:** Resposta GET do DTO **não** deve conter `cpf`, `senha` ou `dataNascimento`.

### Tarefa 10 — Teste Spock de proteção do DTO (Anonimato)

- **O que será feito:** Criar teste específico que valida que os dados sensíveis do recrutador nunca são expostos pelo DTO.
- **Camada:** Backend (Testes)
- **Arquivos:**
  - `src/test/groovy/linketinder/model/RecrutadorDTOSpec.groovy` (novo)
- **Detalhes:**
  - Teste: dado um `Recrutador` com `cpf`, `senha` e `dataNascimento` preenchidos, o método `toDTO()` retorna um objeto sem esses campos.
  - Teste: verificar que o JSON serializado do DTO não contém as chaves `'cpf'`, `'senha'` ou `'dataNascimento'`.
- **Como validar:** Executar o teste e confirmar que os dados sensíveis estão ausentes na projeção.

---

## Tarefas — Frontend

### Tarefa 11 — Interface TypeScript e serviço de recrutadores

- **O que será feito:** Criar a interface `IRecrutador` e o serviço `RecrutadorApiService` para comunicação com a API REST.
- **Camada:** Frontend
- **Arquivos:**
  - `frontend/src/models/index.ts` (alteração)
  - `frontend/src/services/RecrutadorApiService.ts` (novo)
- **Detalhes:**
  - Adicionar interface `IRecrutador` com campos: `id`, `nome`, `email`, `areaRecrutamento`, `descricao`, `idEmpresa`.
  - Criar `RecrutadorApiService.ts` implementando `ICrudService<IRecrutador>`.
  - Implementar `listar()` via GET `/api/recrutadores`.
  - Implementar `adicionar()` via POST `/api/recrutadores` com payload JSON.
  - Implementar `deletar()` via DELETE `/api/recrutadores/{id}`.
- **Como validar:** Compilar com `npm run build` no frontend e verificar que os fetches funcionam contra o backend.

### Tarefa 12 — Página de cadastro de recrutador

- **O que será feito:** Criar `cadastro-recrutador.html` e seu controller TypeScript para o formulário de cadastro.
- **Camada:** Frontend
- **Arquivos:**
  - `frontend/cadastro-recrutador.html` (novo)
  - `frontend/src/controllers/CadastroRecrutadorController.ts` (novo)
  - `frontend/src/pages/cadastroRecrutador/cadastroRecrutador.ts` (novo)
- **Detalhes:**
  - `cadastro-recrutador.html` com campos: nome, e-mail, CPF, senha, data de nascimento, área de recrutamento, descrição.
  - `CadastroRecrutadorController.ts` com validações: e-mail (`RegexValidator`), CPF (`DocumentValidator`), idade >18, senha ≥8 caracteres.
  - `cadastroRecrutador/cadastroRecrutador.ts` como Composition Root (padrão DIP).
  - Ao submeter, chamar `RecrutadorApiService.adicionar()` e redirecionar para o dashboard.
- **Como validar:** Navegar para a página, preencher o formulário e verificar que o recrutador aparece no banco.

### Tarefa 13 — Seção de gerenciamento de recrutadores no Dashboard

- **O que será feito:** Adicionar seção no `dashboard.html` para a empresa master gerenciar recrutadores vinculados.
- **Camada:** Frontend
- **Arquivos:**
  - `frontend/dashboard.html` (alteração)
  - `frontend/src/controllers/DashboardController.ts` (alteração)
- **Detalhes:**
  - Adicionar seção HTML no dashboard com: botão "Criar Recrutador" (link para `cadastro-recrutador.html`), lista de recrutadores vinculados.
  - Atualizar `DashboardController.ts` para carregar e renderizar a lista de recrutadores via `RecrutadorApiService`.
  - Adicionar botão "Remover" em cada recrutador na lista, chamando `RecrutadorApiService.deletar()`.
  - Garantir que o recrutador logado veja o mesmo dashboard de candidatos (anonimato mantido: candidatos como "Candidato Anônimo").
- **Como validar:** Navegar ao dashboard, criar/remover recrutadores e verificar que a lista atualiza corretamente.
- **Verificação do Anonimato:** Candidatos devem continuar sendo exibidos como "Candidato Anônimo" quando visualizados pelo recrutador.

---

## Resumo das Tarefas

| # | Tarefa | Camada | Status |
|---|---|---|---|
| 1 | Script SQL: tabela `recrutadores`, alteração e dados mockados | DB | [x] Concluído |
| 2 | Model `Recrutador` e DTO de projeção | Backend | [x] Concluído |
| 3 | DAO do Recrutador com unicidade global | Backend | [x] Concluído |
| 4 | Service do Recrutador com validações de negócio | Backend | [x] Concluído |
| 5 | Testes Spock do `RecrutadorService` | Backend | [x] Concluído |
| 6 | Alterar `CurtidaDAO` para suportar `id_recrutador` | Backend | [x] Concluído |
| 7 | Alterar `MatchService` para propagar `id_recrutador` | Backend | [x] Concluído |
| 8 | Atualizar testes Spock do `MatchService` | Backend | [x] Concluído |
| 9 | Controller e Servlet do Recrutador | Backend | ⬜ Pendente |
| 10 | Teste Spock de proteção do DTO (Anonimato) | Backend | ⬜ Pendente |
| 11 | Interface TypeScript e serviço de recrutadores | Frontend | ⬜ Pendente |
| 12 | Página de cadastro de recrutador | Frontend | ⬜ Pendente |
| 13 | Seção de gerenciamento de recrutadores no Dashboard | Frontend | ⬜ Pendente |
