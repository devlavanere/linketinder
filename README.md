# Linketinder - MVP

Projeto desenvolvido para o desafio Groovy.
Um sistema de recrutamento que une o modelo do LinkedIn com o formato de "Match" do Tinder.

**Desenvolvedor:** Michel Lavanere Sampaio

## Funcionalidades e Arquitetura

O sistema foi projetado utilizando os pilares de Orientação a Objetos (Herança e Abstração) e estruturado com foco no Princípio da Responsabilidade Única (SRP) para separar a camada de dados da camada de interação.

*   **Banco de Dados em Memória:** Uso de Collections (Listas) para armazenamento rápido dos perfis durante a execução.
*   **Separação de Domínio:** Distinção estrutural entre Pessoa Física (Candidato) e Jurídica (Empresa), herdando atributos comuns de uma base abstrata.
*   **Wizard Interativo:** Fluxo de cadastro guiado passo a passo diretamente no terminal, com tratamento robusto de exceções para prevenir falhas de entrada de dados.
*   **Mock de Dados:** Inicialização do sistema com 5 candidatos e 5 empresas pré-cadastradas para validação imediata do MVP.

## Tecnologias Utilizadas

*   **Groovy:** 4.0.22
*   **Java (JDK):** 8 (Zulu / Azul Systems)
*   **Build Tool:** Gradle

## Como Executar

**Opção 1: Via IDE (IntelliJ IDEA)**
1. Clone este repositório.
2. Aguarde a sincronização do Gradle (download das dependências).
3. Navegue até o arquivo `src/main/groovy/linketinder/app/LinketinderApp.groovy`.
4. Clique no botão de **Run** (Play verde) ao lado da classe principal.
5. Interaja com o Wizard de navegação pelo Console integrado da IDE.

**Opção 2: Via Terminal**
Na raiz do diretório do projeto, execute o comando abaixo utilizando o Gradle Wrapper:
`./gradlew run`