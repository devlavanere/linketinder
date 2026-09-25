<div align="center">
  <img src="https://images.unsplash.com/photo-1555066931-4365d14bab8c?q=80&w=1200&auto=format&fit=crop" alt="Backend and Database Code Banner" style="border-radius: 10px; margin-bottom: 20px;" />
</div>

# Linketinder - MVP & Integração JDBC

Projeto Full-Stack desenvolvido para o desafio ZG-HERO.
Um sistema inovador de recrutamento que une o modelo de networking do LinkedIn com a dinâmica ágil de "Match" do Tinder, aplicando o conceito de recrutamento às cegas para focar em competências.

**Desenvolvedor:** Michel Lavanere Sampaio

---

## Funcionalidades e Arquitetura

O sistema evoluiu de um armazenamento volátil em memória para uma infraestrutura robusta conectada a um banco de dados relacional.

### 1. Backend (Groovy + JDBC)
O sistema foi projetado utilizando os pilares da Orientação a Objetos, estruturado no padrão **MVC (Model-View-Controller)** e respeitando o Princípio da Responsabilidade Única (SRP).
* **Camada de Visão (View):** Menus interativos totalmente modularizados no pacote `view`, isolando a lógica de console por entidade (`CandidatoView`, `EmpresaView`, `VagaView`).
* **Design Pattern DAO:** Criação de Data Access Objects (`CandidatoDAO`, `EmpresaDAO`, `VagaDAO`) para isolar a lógica de acesso aos dados.
* **Integração Nativa:** Conexão JDBC pura com o PostgreSQL, praticando a escrita de Queries SQL diretas (sem ORM).
* **Resolução Relacional:** Tratamento dinâmico de relacionamentos (1:N e N:N) através de `JOINs` para reconstruir os objetos de negócio durante as listagens.

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
* **Cadastro:** Formulários interativos para inclusão de perfis (Candidato e Empresa).
* **Dashboard Corporativo:** Visão que lista candidatos anonimamente e exibe um gráfico dinâmico (Chart.js) com as competências mais procuradas.
* **Mural de Vagas:** Visão do candidato que lista as vagas ocultando o nome do empregador.
* *Nota:* A persistência no frontend nesta etapa ainda é gerenciada via LocalStorage (desacoplada do banco atual).

---

## Tecnologias Utilizadas

**Backend & Dados:**
* Groovy (4.0.22) & Java JDK 8 (Zulu)
* PostgreSQL
* JDBC Driver (org.postgresql)
* Spock Framework (2.3)
* Gradle

**Frontend:**
* TypeScript
* HTML5 & CSS3
* Vite
* Chart.js

---

## Como Executar a Aplicação

O projeto requer que o banco de dados seja inicializado antes do backend.

### Passo 1: Inicializando o Banco de Dados
1. No seu client do PostgreSQL, crie um banco vazio: `CREATE DATABASE linketinder;`
2. Execute o arquivo `database/linketinder_init.sql` para gerar a estrutura e os mocks básicos.
3. Execute o arquivo `database/linketinder_match.sql` para gerar a lógica de cruzamento de perfis.

### Passo 2: Rodando o Backend (Console)
Acesse a pasta do backend, certifique-se de que os dados de usuário/senha estão corretos na classe `DatabaseConnection.groovy`, e execute via Gradle:
```bash
cd backend
./gradlew run