# Linketinder - MVP

Projeto desenvolvido para o desafio Groovy.
Um sistema de recrutamento que une o modelo do LinkedIn com o formato de "Match" do Tinder.

**Desenvolvedor:** Michel Lavanere Sampaio

## Funcionalidades e Arquitetura

### Backend (Console Groovy)
O sistema foi projetado utilizando os pilares de Orientação a Objetos e estruturado com foco no Princípio da Responsabilidade Única (SRP).
*   **Arquitetura em Camadas:** Divisão entre Modelos (`Pessoa`, `Candidato`, `Empresa`), Regras de Negócio (`GerenciadorDePerfis`) e Apresentação (`Menu`).
*   **Testes Unitários (TDD):** Desenvolvimento guiado por testes com Spock Framework, garantindo a integridade de inserção de novos elementos.
*   **Banco de Dados em Memória:** Uso de Collections para armazenamento isolado e seguro pelo gerenciador.
*   **Mock de Dados:** Inicialização com 5 candidatos e 5 empresas pré-cadastradas para validação imediata do MVP.

### Frontend (Web)
O MVP visual é composto por 4 telas principais com garantia de anonimato até o "match":
*   **Cadastro de Candidato & Empresa:** Formulários independentes e interativos para inclusão de perfis.
*   **Perfil da Empresa (Dashboard):** Visão corporativa que lista candidatos de forma anônima e exibe um gráfico de barras dinâmico com as competências mais procuradas.
*   **Perfil do Candidato (Mural):** Visão do desenvolvedor que lista as vagas disponíveis ocultando o nome da empresa empregadora ("Empresa Confidencial").
*   **Isolamento Inicial:** Persistência de dados gerenciada via LocalStorage (desacoplado do backend nesta etapa).

## Tecnologias Utilizadas

**Backend:**
*   Groovy (4.0.22) & Java JDK 8 (Zulu)
*   Spock Framework (2.3)
*   Gradle

**Frontend:**
*   TypeScript
*   HTML5 & CSS3
*   Vite
*   Chart.js

## Como Executar a Aplicação

### Rodando o Backend (Console)
Acesse a pasta do backend e execute via Gradle Wrapper:
```bash
cd backend
./gradlew run