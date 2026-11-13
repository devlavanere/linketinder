# Linketinder — AGENTS.md

Guia de referência para agentes de IA que trabalham neste repositório.

---

## 1. Visão Geral

Linketinder é um sistema full-stack de recrutamento que combina o modelo de networking do LinkedIn com a dinâmica de "Match" do Tinder. O conceito central é o **recrutamento às cegas**: candidatos e empresas se avaliam com base em competências, sem conhecer a identidade um do outro até que haja interesse mútuo (match).

O projeto foi desenvolvido para o desafio **ZG-HERO** e segue rigorosamente os princípios **SOLID**, **Clean Architecture** e o padrão **MVC**.

---

## 2. Stack Tecnológica

### Backend
| Tecnologia | Versão |
|---|---|
| Groovy | 4.0.22 |
| Java JDK | 8 (Zulu) |
| PostgreSQL | — |
| JDBC Driver | 42.7.12 |
| Tomcat (via Gretty) | 9 |
| Spock Framework | 2.3-groovy-4.0 |
| Gradle | — |

### Frontend
| Tecnologia | Versão |
|---|---|
| TypeScript | — |
| Vite | — |
| Chart.js | — |
| HTML5 / CSS3 | Sem frameworks de estilo |

---

## 3. Estrutura do Projeto

```
linketinder/
├── build.gradle              # Configuração Gradle (Groovy, Spock, JDBC, Gretty)
├── settings.gradle
├── gradlew / gradlew.bat     # Wrapper Gradle
├── database/                 # Scripts SQL de inicialização e match
│   ├── linketinder_init.sql  # DDL + DML: tabelas principais e dados mockados
│   ├── linketinder_match.sql # Tabelas de curtidas/matches + lógica SQL de match
│   └── README.MD
├── frontend/                 # Frontend web (SPA com Vite)
│   ├── index.html            # Ponto de entrada / Home
│   ├── dashboard.html        # Dashboard corporativo (visão da empresa)
│   ├── vagas.html            # Mural de vagas (visão do candidato)
│   ├── cadastro-candidato.html
│   ├── cadastro-empresa.html
│   ├── login.html
│   ├── package.json
│   └── src/
│       ├── models/           # Interfaces TypeScript (IPessoa, ICandidato, IEmpresa, IVaga)
│       ├── services/         # Camada de persistência (LocalStorage + API)
│       │   └── interfaces/   # Contratos (ICrudService, IAuthService)
│       ├── controllers/      # Orquestração com injeção de dependência
│       ├── validators/       # RegexValidator, DocumentValidator
│       └── pages/            # Composition Root por página
├── src/                      # Backend Groovy
│   ├── main/groovy/linketinder/
│   │   ├── model/            # Entidades (Pessoa, Candidato, Empresa, Vaga, Competencia)
│   │   ├── dao/              # Data Access Objects + ConnectionFactory (Singleton)
│   │   │   └── interfaces/   # ICrudDAO, IMatchDAO, IDatabaseConnection
│   │   ├── service/          # Regras de negócio (CandidatoService, EmpresaService, VagaService, MatchService)
│   │   ├── controller/       # Orquestradores (CandidatoController, EmpresaController, VagaController, MatchController)
│   │   ├── view/             # Menus interativos (CandidatoView, EmpresaView, VagaView, MenuPrincipal)
│   │   ├── api/              # Servlets REST (CandidatoServlet, EmpresaServlet, VagaServlet)
│   │   └── app/              # Entradas (LinketinderApp, GerenciadorDePerfis — Facade)
│   └── test/groovy/linketinder/
│       └── service/          # Testes Spock (CandidatoServiceSpec, EmpresaServiceSpec, VagaServiceSpec, MatchServiceSpec)
└── bin/                      # Scripts auxiliares
```

---

## 4. Domínio do Linketinder

### Entidades

| Entidade | Descrição | Atributos principais |
|---|---|---|
| **Pessoa** | Classe abstrata base | `id`, `nome`, `email`, `senha`, `pais`, `cep`, `descricao` |
| **Candidato** | Estende Pessoa | `cpf`, `sobrenome`, `dataNascimento`, `competencias[]` |
| **Empresa** | Estende Pessoa | `cnpj` |
| **Vaga** | Oferta de emprego vinculada a uma empresa | `id`, `idEmpresa`, `nome`, `descricao`, `local`, `competencias[]` |
| **Competencia** | Habilidade/skill | `id`, `nome` |
| **Curtida** | Interesse unidirecional (duas tabelas) | `curtidas_candidatos(id_candidato, id_vaga)`, `curtidas_empresas(id_empresa, id_candidato)` |
| **Match** | Resultado do interesse mútuo | `id`, `id_candidato`, `id_vaga` |

### Relacionamentos

- **Candidato** `1:N` **Competencia** (via tabela `candidato_competencia`)
- **Vaga** `1:N` **Competencia** (via tabela `vaga_competencia`)
- **Empresa** `1:N` **Vaga** (FK `vagas.id_empresa → empresas.id`)
- **Candidato** `N:N` **Vaga** via **Curtida** e **Match**
- **Empresa** `N:N` **Candidato** via **Curtida**

---

## 5. Regras de Negócio Centrais

### 5.1 Curtida (Like)

O sistema possui **duas direções de curtida**:

1. **Candidato curte Vaga** (`candidatoCurteVaga`):
   - O candidato expressa interesse em uma vaga.
   - A curtida é registrada em `curtidas_candidatos`.
   - O sistema verifica se a empresa dona da vaga já curtiu esse candidato.
   - Se sim → **Match é criado** na tabela `matches`.

2. **Empresa curte Candidato** (`empresaCurteCandidato`):
   - A empresa expressa interesse em um candidato.
   - A curtida é registrada em `curtidas_empresas`.
   - O sistema busca todas as vagas dessa empresa que o candidato já curtiu.
   - Para cada vaga encontrada → **Match é criado**.

### 5.2 Match

- O match só ocorre quando há **interesse mútuo**: o candidato curtiu a vaga **E** a empresa curtiu o candidato.
- O match é registrado na tabela `matches(id_candidato, id_vaga)` com `ON CONFLICT DO NOTHING` para evitar duplicação.
- Uma empresa pode curtir um candidato e gerar matches em **múltiplas vagas** simultaneamente, se o candidato tiver curtido mais de uma vaga da mesma empresa.
- A lógica de match está implementada em duas camadas:
  - **Backend**: `CurtidaDAO.curtirVaga()` e `CurtidaDAO.curtirCandidato()`
  - **Banco**: `linketinder_match.sql` com query SQL de cruzamento

### 5.3 Princípio do Anonimato — RESTRIÇÃO EXPLÍCITA

> **REGRAS OBRIGATÓRIAS — qualquer nova feature DEVE respeitá-las:**

1. **Antes do match**, o candidato é exibido para a empresa como **"Candidato Anônimo"**. São visíveis apenas:
   - Idade
   - Lista de competências
   - Descrição profissional (via tooltip)
   - Localização (via tooltip)
   - **NÃO são visíveis**: nome, sobrenome, email, CPF.

2. **Antes do match**, a empresa é exibida para o candidato como **"🏢 Empresa Confidencial"**. São visíveis apenas:
   - Título da vaga
   - Descrição da vaga
   - Competências exigidas
   - **NÃO são visíveis**: nome da empresa, CNPJ, email.

3. **Após o match**, as identidades são reveladas mutuamente.

4. **Implementação atual do anonimato:**
   - Frontend — `DashboardController.ts`: candidatos renderizados como `"Candidato Anônimo"` (linha 58).
   - Frontend — `VagasController.ts`: empresas renderizadas como `"🏢 Empresa Confidencial"` (linha 51).
   - Backend — os Servlets e Services devem filtrar os dados sensíveis antes de retornar para o frontend.

5. **Qualquer agente que implementar uma nova feature deve garantir que os dados sensíveis (nome, email, CPF, CNPJ) nunca sejam expostos antes do match.**

---

## 6. Banco de Dados

### Scripts SQL

| Arquivo | Propósito |
|---|---|
| `database/linketinder_init.sql` | Cria as tabelas principais (`candidatos`, `empresas`, `competencias`, `vagas`, `candidato_competencia`, `vaga_competencia`) e insere dados mockados. |
| `database/linketinder_match.sql` | Cria as tabelas de curtidas e matches (`curtidas_candidatos`, `curtidas_empresas`, `matches`) e insere dados de exemplo com a query SQL de match. |

### Como aplicar

```bash
# 1. Crie o banco vazio no PostgreSQL:
CREATE DATABASE linketinder;

# 2. Execute os scripts em ordem:
psql -d linketinder -f database/linketinder_init.sql
psql -d linketinder -f database/linketinder_match.sql
```

### Configuração de conexão

O backend lê as credenciais via variáveis de ambiente com fallback:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/linketinder` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `lavanere` |

Arquivo: `src/main/groovy/linketinder/dao/DbConfig.groovy`

---

## 7. Como Executar

### Backend (Console + Servlet)

```bash
# Pré-requisito: banco de dados inicializado (ver seção 6)

# Executar o console interativo:
./gradlew run

# Executar o servidor Tomcat com Servlets (porta 8080):
./gradlew appRun
```

O servidor Tomcat é gerenciado pelo plugin **Gretty** (Tomcat 9, porta 8080, context path `/`).

### Frontend (Web)

```bash
cd frontend
npm install
npm run dev
```

O frontend usa **LocalStorage** como persistência padrão (MVP). Através do DIP (Inversão de Dependência), pode ser configurado para consumir a API REST do backend substituindo a injeção de serviços.

---

## 8. Como Testar

### Backend — Spock Framework

```bash
# Executar todos os testes:
./gradlew test

# Executar um teste específico:
./gradlew test --tests "linketinder.service.MatchServiceSpec"
```

Os testes usam **mocks de interface** (graças ao DIP) e são puramente independentes da infraestrutura. Os specs cobrem:
- `CandidatoServiceSpec` — validações de CRUD de candidatos
- `EmpresaServiceSpec` — validações de CRUD de empresas
- `VagaServiceSpec` — validações de CRUD de vagas
- `MatchServiceSpec` — validações de curtida e match
- `GerenciadorDePerfisSpec` — orquestração da Facade

### Padrão de teste Spock

```groovy
def "Deve [comportamento esperado]"() {
    given: "Um mock da interface"
    def daoMock = Mock(IMatchDAO)
    def service = new MatchService(daoMock)

    when: "Ação é executada"
    boolean result = service.candidatoCurteVaga(1, 1)

    then: "Resultado esperado"
    1 * daoMock.curtirVaga(1, 1) >> true
    result == true
}
```

---

## 9. Convenções e Padrões

### 9.1 Arquitetura

- **MVC estrito**: View → Controller → Service → DAO. Nenhuma regra de negócio na View.
- **Facade**: `GerenciadorDePerfis` isola as Views da complexidade dos subsistemas.
- **Factory Method**: `ConnectionFactory` abstrai a criação de conexões JDBC.
- **Singleton**: `ConnectionFactory` usa `@Singleton` do Groovy para um único ponto de acesso global.

### 9.2 SOLID

- **SRP**: Models (estado), Services (regras), DAOs (persistência) são estritamente separados.
- **OCP**: Contratos (interfaces) permitem expansão sem modificar código existente.
- **LSP**: Herança correta (`Candidato extends Pessoa`, `Empresa extends Pessoa`).
- **ISP**: Interfaces coesas (`ICrudDAO`, `IMatchDAO`, `ICrudService`, `IAuthService`).
- **DIP**: Classes de alto nível dependem de interfaces injetadas via construtor.

### 9.3 Nomenclatura

- **Classes Groovy**: `PascalCase` (ex: `CandidatoService`, `CurtidaDAO`).
- **Variáveis Groovy**: `camelCase` (ex: `idCandidato`, `isMatch`).
- **Tabelas SQL**: `snake_case` no plural (ex: `curtidas_candidatos`, `candidato_competencia`).
- **Colunas SQL**: `snake_case` (ex: `id_candidato`, `data_nascimento`).
- **TypeScript**: `PascalCase` para classes/interfaces, `camelCase` para variáveis.
- **Pacotes**: `linketinder.<camada>` (ex: `linketinder.model`, `linketinder.service`).

### 9.4 Tratamento de Erros

- Services lançam `RuntimeException` com mensagens descritivas em português.
- DAOs capturam `SQLException` e re-lançam como `RuntimeException`.
- Validação **Fail-Fast**: IDs inválidos são rejeitados antes de qualquer acesso ao banco.

### 9.5 Restrições

- **Nunca exponha dados sensíveis antes do match** (Princípio do Anonimato — ver seção 5.3).
- **Nunca modifique a string de conexão ou o driver JDBC nos DAOs** — use `ConnectionFactory`.
- **Nunca instancie DAOs diretamente nos Services** — use injeção via construtor.
- **Sempre use `ON CONFLICT DO NOTHING`** em inserts de curtidas e matches para evitar duplicação.
