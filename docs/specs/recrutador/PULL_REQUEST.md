# feat: módulo de recrutador corporativo com anonimato

**Closes #3**

---

## Resumo da Implementação

Esta PR entrega o módulo completo de **Recrutador Corporativo** para o Linketinder, permitindo que empresas cadastrem recrutadores delegados que atuam como seu "braço" no sistema — curtindo candidatos e gerenciando vagas — sem expor dados sensíveis, em conformidade com o **Princípio do Anonimato**.

A feature foi desenvolvida em **13 tarefas incrementais**, organizadas em três fases: Banco de Dados, Backend (com testes Spock) e Frontend.

---

## Backend

### Camada de Modelo (Model)

- **`Recrutador.groovy`** — Entidade independente (não estende `Pessoa`) com atributos: `id`, `nome`, `email`, `cpf`, `senha`, `dataNascimento`, `areaRecrutamento`, `descricao`, `idEmpresa`. Inclui método `getIdade()` com cálculo via `Period.between`.
- **`RecrutadorDTO.groovy`** — Projeção segura com apenas `id`, `nome`, `areaRecrutamento`, `descricao`, `idEmpresa`. O método `toDTO()` em `Recrutador` converte o model para o DTO, filtrando `cpf`, `senha` e `dataNascimento`.

### Camada de Persistência (DAO)

- **`RecrutadorDAO.groovy`** — CRUD completo com `RETURN_GENERATED_KEYS`. Consultas de unicidade global: `emailJaEmUso()` cruza candidatos, empresas e recrutadores; `cpfJaEmUso()` cruza candidatos e recrutadores. Método `buscarPorId()` para localização do recrutador no fluxo de match.
- **`CurtidaDAO.groovy`** — Alterado para aceitar `id_recrutador` opcional (nullable) em `curtirCandidato(int, int, Integer)`. O método antigo delega ao novo com `null`, mantendo compatibilidade retroativa.
- **`IMatchDAO.groovy`** — Interface atualizada com a nova assinatura de `curtirCandidato()`.

### Camada de Negócio (Service)

- **`RecrutadorService.groovy`** — Validações rigorosas: e-mail por Regex, CPF com cálculo de dígitos verificadores (Módulo 11), rejeição de CPFs repetidos, data de nascimento não nula e não futura, idade ≥18 anos, senha forte (≥8 caracteres com maiúscula, minúscula, número e caractere especial) e unicidade global de e-mail e CPF.
- **`MatchService.groovy`** — Novo método `recrutadorCurteCandidato(int, int)` que busca o recrutador, extrai o `idEmpresa` e delega para `matchDao.curtirCandidato(idEmpresa, idCandidato, idRecrutador)`. Construtor sobrecarregado com `IRecrutadorDAO` opcional para compatibilidade retroativa.

### Camada de API (Servlet)

- **`RecrutadorServlet.groovy`** — Mapeado em `/api/recrutadores` com CORS headers (`Access-Control-Allow-Origin: *`).
  - `POST` — Cadastrar recrutador, retorna 201/400.
  - `GET` — Listar recrutadores por `idEmpresa`, retorna **DTOs em JSON** (nunca dados sensíveis).
  - `DELETE` — Remover recrutador por ID, retorna 204.
  - `OPTIONS` — CORS preflight, retorna 200.

### Banco de Dados

- **`database/linketinder_recrutador.sql`** — Cria a tabela `recrutadores` com FK para `empresas(id)` ON DELETE CASCADE. Adiciona coluna `id_recrutador` nullable em `curtidas_empresas` com FK e ON DELETE SET NULL. Inclui dados mockados (2 recrutadores vinculados a empresas diferentes).

---

## Frontend

### Interfaces TypeScript

- **`IRecrutador`** — Reflete o DTO do backend: `id`, `nome`, `areaRecrutamento`, `descricao`, `idEmpresa` (sem dados sensíveis).
- **`IRecrutadorCadastro`** — Modelo de escrita para POST: `nome`, `email`, `cpf`, `senha`, `dataNascimento`, `areaRecrutamento`, `descricao`, `idEmpresa`.

### Serviço de API

- **`RecrutadorApiService.ts`** — Comunicação REST com o backend. `listarPorEmpresa(idEmpresa)` via GET, `adicionar(recrutador)` via POST com body JSON, `deletar(id)` via DELETE. Tratamento de erros com `console.error` e `throw`.

### Controller de Cadastro

- **`CadastroRecrutadorController.ts`** — Validações fortes alinhadas ao backend: nome (RegexValidator), e-mail (RegexValidator), CPF com dígitos verificadores (DocumentValidator), senha forte (≥8 chars, maiúscula, minúscula, número, especial) e idade ≥18 anos. Chama `RecrutadorApiService.adicionar()` e redireciona para `dashboard.html`.

### Dashboard da Empresa

- **`dashboard.html`** — Nova seção "Meus Recrutadores" com grid de cards e botão "Cadastrar Novo Recrutador" (link para `cadastro-recrutador.html`).
- **`DashboardController.ts`** — Recebe `RecrutadorApiService` via injeção de dependência (DIP). Método `carregarRecrutadores(idEmpresa)` consome o endpoint GET e renderiza cards dinâmicos com nome, área de recrutamento e descrição.

### Composition Root

- **`cadastroRecrutador/cadastroRecrutador.ts`** — Instancia o serviço, injeta no controller via construtor (DIP) e chama `iniciar()`.
- **`dashboard/dashboard.ts`** — Instancia `RecrutadorApiService` e o injeta como quinto argumento do `DashboardController`.

---

## Princípio do Anonimato

O Princípio do Anonimato é a regra inegociável do Linketinder. O módulo de recrutador o respeita em três camadas:

### 1. DTO de Projeção no Backend

O `RecrutadorDTO` contém apenas os campos públicos (`id`, `nome`, `areaRecrutamento`, `descricao`, `idEmpresa`). Os dados sensíveis (`cpf`, `senha`, `dataNascimento`) são **excluídos por design** — a classe não os declara como propriedades. O método `toDTO()` em `Recrutador` realiza a conversão segura.

### 2. Servlet Retorna Apenas DTOs

O `RecrutadorServlet.doGet()` mapeia a lista de `Recrutador` para `RecrutadorDTO` antes de serializar o JSON. O `JsonOutput.toJson()` opera sobre os DTOs, não sobre os models completos.

### 3. Validação por Teste Spock

O `RecrutadorDTOSpec.groovy` contém 3 testes BDD que comprovam a blindagem:

1. **Conversão `toDTO()`** — Dado um `Recrutador` com CPF, senha e data de nascimento preenchidos, o DTO resultante contém apenas os 5 campos públicos.
2. **Reflexão Groovy** — Verifica que `RecrutadorDTO` não possui propriedades `cpf`, `senha` ou `dataNascimento` via introspecção (`hasProperty`).
3. **Serialização JSON** — Serializa o DTO com `JsonOutput` e confirma que as chaves `"cpf"`, `"senha"` e `"dataNascimento"` **não aparecem** na string JSON resultante.

Além disso, o candidato **nunca** vê o nome do recrutador — antes ou após o match. O recrutador atua em nome da empresa, e o candidato continua vendo "🏢 Empresa Confidencial".

---

## Critérios de Aceite

### Cadastro e Vínculo

- [x] A empresa master consegue criar um recrutador com nome, e-mail, CPF, senha, data de nascimento e área de recrutamento.
- [x] O recrutador é vinculado automaticamente à empresa que o criou (`id_empresa`).
- [x] O e-mail do recrutador não pode ser duplicado em todo o sistema (candidatos, empresas, outros recrutadores).
- [x] O CPF do recrutador não pode ser duplicado em todo o sistema (candidatos e outros recrutadores).
- [x] O recrutador deve ter mais de 18 anos no momento do cadastro.
- [x] A senha do recrutador deve ter no mínimo 8 caracteres.
- [x] A remoção do recrutador não afeta as curtidas, matches ou vagas já registradas.

### Curtida e Match

- [x] O recrutador consegue curtir candidatos no dashboard.
- [x] A curtida do recrutador é registrada em `curtidas_empresas` com `id_empresa` correto e `id_recrutador` preenchido.
- [x] Se a empresa já curtiu o candidato (por outro meio), a curtida duplicada é ignorada (`ON CONFLICT DO NOTHING`).
- [x] Se o candidato já curtiu uma vaga da empresa, o match é criado corretamente.
- [x] A curtida da empresa master (sem recrutador) continua funcionando com `id_recrutador = NULL`.

### Princípio do Anonimato

- [x] O candidato **nunca** vê o nome, e-mail, CPF ou data de nascimento do recrutador antes do match.
- [x] O candidato **nunca** vê o nome do recrutador mesmo após o match (apenas a empresa é revelada).
- [x] O recrutador vê o candidato como **"Candidato Anônimo"** antes do match (apenas idade, competências, descrição e localização).
- [x] Após o match, o recrutador vê nome e e-mail do candidato.
- [x] Os endpoints da API **nunca** retornam CPF, data de nascimento ou senha do recrutador (validado por DTO de projeção).
- [x] Existe um teste Spock específico que valida que os dados sensíveis do recrutador não são expostos pela API.

### Banco de Dados

- [x] O script `database/linketinder_recrutador.sql` cria a tabela `recrutadores` sem erros.
- [x] A coluna `id_recrutador` é adicionada a `curtidas_empresas` como nullable.
- [x] Os dados mockados incluem 2 recrutadores vinculados a empresas diferentes.
- [x] A constraint `ON DELETE SET NULL` em `id_recrutador` funciona corretamente ao remover um recrutador.

### Testes

- [x] `RecrutadorServiceSpec.groovy` cobre: cadastro, validação de unicidade global, validação de idade, validação de senha (13 testes BDD).
- [x] `MatchServiceSpec.groovy` é atualizado para cobrir curtida com `id_recrutador` preenchido e `NULL` (5 testes BDD).
- [x] `RecrutadorDTOSpec.groovy` cobre: conversão `toDTO()`, reflexão Groovy e serialização JSON (3 testes BDD).
- [x] Todos os testes existentes continuam passando (regressão).

---

## Como Testar

### Pré-requisitos

1. PostgreSQL rodando localmente na porta 5432.
2. Banco `linketinder` criado e scripts aplicados em ordem:

```bash
psql -d linketinder -f database/linketinder_init.sql
psql -d linketinder -f database/linketinder_match.sql
psql -d linketinder -f database/linketinder_recrutador.sql
```

### 1. Testes Unitários (Backend)

```bash
# Todos os testes, incluindo os novos:
./gradlew test

# Testes específicos do recrutador:
./gradlew test --tests "linketinder.service.RecrutadorServiceSpec"
./gradlew test --tests "linketinder.model.RecrutadorDTOSpec"
./gradlew test --tests "linketinder.service.MatchServiceSpec"
```

### 2. Servidor Backend

```bash
./gradlew appRun
```

O Tomcat inicia na porta 8080 com context path `/`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

### 4. Fluxo de Cadastro de Recrutador

1. Acesse o dashboard da empresa (`http://localhost:5173/dashboard.html`).
2. Clique em **"Cadastrar Novo Recrutador"** na seção "Meus Recrutadores".
3. Preencha o formulário com dados válidos:
   - Nome: "Ana Silva"
   - E-mail: "ana.silva@empresa.com"
   - CPF: "12345678901"
   - Senha: "Senha@123"
   - Data de Nascimento: "1995-06-15"
   - Área de Recrutamento: "Engenharia de Software"
   - Descrição: "Recrutadora sênior com 5 anos de experiência"
4. Clique em **"Salvar Recrutador"**.
5. Ao retornar ao dashboard, o recrutador deve aparecer na seção "Meus Recrutadores".

### 5. Verificação do Anonimato via API

```bash
# Liste os recrutadores — a resposta NÃO deve conter cpf, senha ou dataNascimento:
curl -s http://localhost:8080/api/recrutadores?idEmpresa=1 | python3 -m json.tool

# Resposta esperada (apenas campos públicos):
# [ { "id": 1, "nome": "Ana Silva", "areaRecrutamento": "Engenharia de Software", "descricao": "...", "idEmpresa": 1 } ]
```

### 6. Verificação de Validações

Tente cadastrar um recrutador com:
- CPF inválido (menos de 11 dígitos) → deve falhar com mensagem de erro.
- E-mail duplicado (já usado por candidato ou outro recrutador) → deve falhar.
- Menor de 18 anos → deve falhar.
- Senha fraca (sem caractere especial) → deve falhar.

---

## Arquitetura e Padrões Aplicados

- **MVC estrito**: Model → DAO → Service → Controller → Servlet.
- **DIP (Inversão de Dependência)**: Services recebem DAOs via construtor; Controllers do frontend recebem serviços via construtor.
- **DTO de Projeção**: `RecrutadorDTO` filtra dados sensíveis por design.
- **Fail-Fast**: Validações de IDs inválidos antes de qualquer acesso ao banco.
- **ON CONFLICT DO NOTHING**: Evita duplicação em curtidas e matches.
- **Compatibilidade Retroativa**: Construtores sobrecarregados e métodos delegados preservam o código existente.
- **Composition Root**: Cada página frontend tem seu próprio arquivo `.ts` que instancia e injeta as dependências.

---

## Commits da Feature

| Commit | Descrição |
|---|---|
| `ae75966` | feat(db): cria script .sql de recrutadores e ajusta tabela de curtidas (Tarefa 1) |
| `b98ec40` | feat(backend): cria Model e DTO de protecao de Recrutador (Tarefa 2) |
| `6339013` | feat(backend): cria RecrutadorDAO com validacoes globais de unicidade (Tarefa 3) |
| `e425398` | feat(backend): aplica interfaces e Inversao de Dependencia no Service e DAO |
| `3b0bc65` | test(backend): cria testes Spock para RecrutadorService cobrindo validacoes de seguranca (Tarefa 5) |
| `2b45d3f` | feat(backend): atualiza IMatchDAO e CurtidaDAO para registrar id_recrutador opcional (Tarefa 6) |
| `57cbe55` | feat(backend): implementa recrutadorCurteCandidato no MatchService (Tarefa 7) |
| `446ef4f` | test(backend): atualiza MatchServiceSpec para cobrir curtidas feitas por recrutadores (Tarefa 8) |
| `c139ada` | feat(api): cria controller e servlet rest para recrutador blindado por DTO (Tarefa 9) |
| `833fbd4` | test(backend): cria testes Spock provando a blindagem de dados no RecrutadorDTO (Tarefa 10) |
| `ef45899` | feat(frontend): cria interfaces TS e RecrutadorApiService para comunicacao REST (Tarefa 11) |
| `1c80b94` | feat(frontend): integra RecrutadorApiService ao Dashboard para exibir recrutadores da empresa (Tarefa 13) |
| `455d995` | feat(frontend): adiciona botao de navegacao para cadastro de recrutador no dashboard |
