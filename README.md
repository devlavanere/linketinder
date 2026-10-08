<div align="center">
  <img src="https://images.unsplash.com/photo-1555066931-4365d14bab8c?q=80&w=1200&auto=format&fit=crop" alt="Backend and Database Code Banner" style="border-radius: 10px; margin-bottom: 20px;" />
</div>

# Linketinder - MVP & Integração JDBC

Projeto Full-Stack desenvolvido para o desafio ZG-HERO.
Um sistema inovador de recrutamento que une o modelo de networking do LinkedIn com a dinâmica ágil de "Match" do Tinder, aplicando o conceito de recrutamento às cegas para focar em competências.

**Desenvolvedor:** Michel Lavanere Sampaio

---

##  Refatoração e Clean Code (Release v1.0.0)
Nesta versão, a aplicação passou por uma refatoração baseada nos princípios do **Clean Code** (Robert C. Martin) e **SOLID**, elevando a qualidade e manutenibilidade do software:

1. **Separação de Responsabilidades (SRP) e Padrão Facade:**
    * **Problema:** A classe `GerenciadorDePerfis` atuava como uma *God Class* e as Views lidavam com lógicas de persistência.
    * **Solução:** Criação da camada `Service` para isolar exclusivamente as regras de negócio (validações de domínio, idade, e-mail). A classe `GerenciadorDePerfis` foi convertida no padrão **Facade**, orquestrando as chamadas entre a View e os Services, garantindo o Baixo Acoplamento.
2. **Exception Translation e Fail-Fast:**
    * **Problema:** Erros de banco (`SQLException`) eram silenciados por `println` nos DAOs.
    * **Solução:** DAOs agora capturam erros técnicos e lançam `RuntimeException`. As Views capturam essas exceções e exibem mensagens semânticas (Fail-Fast), blindando a aplicação contra estados inconsistentes.
3. **Funções Pequenas e DRY:**
    * **Problema:** Métodos longos misturando queries SQL, montagem de objetos e regras de Match.
    * **Solução:** Extração de sub-rotinas (ex: `mapearCandidato()`, `registrarMatch()`), eliminando repetição de código (DRY) e deixando a leitura fluida como uma narrativa (Regra de Funções Pequenas).
4. **Relacionamentos N:N Automatizados:**
    * Refatoração dos DAOs utilizando `Statement.RETURN_GENERATED_KEYS` para capturar IDs em tempo de inserção e persistir dinamicamente chaves estrangeiras nas tabelas associativas (`candidato_competencia` e `vaga_competencia`).
5. **Cobertura de Testes (BDD):**
    * Migração de testes defasados para uma suíte isolada utilizando **Spock Framework** e **Byte Buddy** para Mock de classes concretas, garantindo 100% de validação na camada Service sem poluir o banco de dados.

---

## Funcionalidades e Arquitetura

O sistema evoluiu de um armazenamento volátil para uma arquitetura em camadas robusta conectada a um banco de dados relacional.

### 1. Backend (Groovy + JDBC)
O sistema foi projetado utilizando os pilares da Orientação a Objetos, estruturado no padrão **Clean Architecture** e respeitando o Princípio da Responsabilidade Única (SRP).
- **Camada de Visão (View):** Menus interativos totalmente modularizados, isolando a lógica de console por entidade (`CandidatoView`, `EmpresaView`, `VagaView`).
- **Camada de Serviço (Service):** Detentora do coração do sistema, validando fluxos de negócio.
- **Design Pattern DAO:** Criação de Data Access Objects (`CandidatoDAO`, `EmpresaDAO`, `VagaDAO`, `CurtidaDAO`) para isolar a lógica de acesso aos dados.
- **Integração Nativa:** Conexão JDBC pura com o PostgreSQL, praticando a escrita de Queries SQL diretas (sem ORM).

### 2. Arquitetura de Dados (PostgreSQL)
A modelagem seguiu as regras de normalização (1FN até 3FN). Todo o detalhamento, scripts DDL/DML e lógica de Match encontram-se documentados na [Pasta Database](./database).

<div align="center">
  <a href="./database">
    <img src="database/assets/modelo_logico_match.png" alt="Modelo Lógico - Clique para ver mais" width="60%" />
  </a>
  <p><i>Acesse a pasta /database para ver os scripts e a Lógica de Match detalhada.</i></p>
</div>

### 3. Frontend (Web)
O MVP visual é composto por telas independentes com garantia de anonimato até o "match":
- **Cadastro:** Formulários interativos para inclusão de perfis (Candidato e Empresa).
- **Dashboard Corporativo:** Visão que lista candidatos anonimamente e exibe um gráfico dinâmico (Chart.js) com as competências mais procuradas.
- **Mural de Vagas:** Visão do candidato que lista as vagas ocultando o nome do empregador.
- *Nota:* A persistência no frontend nesta etapa ainda é gerenciada via LocalStorage (desacoplada do banco atual).

---

## Tecnologias Utilizadas

**Backend & Dados:**
- Groovy (4.0.22) & Java JDK 8 (Zulu)
- PostgreSQL
- JDBC Driver (org.postgresql)
- Spock Framework (2.3)
- Byte Buddy (1.14.10) - *Mocking engine*
- Gradle

**Frontend:**
- TypeScript
- HTML5 & CSS3
- Vite
- Chart.js

---

## Como Executar a Aplicação

O projeto requer que o banco de dados seja inicializado antes do backend.

### Passo 1: Inicializando o Banco de Dados
1. No seu client do PostgreSQL, crie um banco vazio: `CREATE DATABASE linketinder;`
2. Execute o arquivo `database/linketinder_init.sql` para gerar a estrutura e os mocks básicos.
3. Execute o arquivo `database/linketinder_match.sql` para gerar a lógica de cruzamento de perfis.

### Passo 2: Rodando o Backend (Console)
Acesse a pasta do backend, certifique-se de que os dados de usuário/senha estão configurados adequadamente em seu ambiente, e execute via Gradle:
```bash
./gradlew run