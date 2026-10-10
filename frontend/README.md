# Linketinder - Frontend MVP

Interface web construída para o projeto Linketinder, aplicando conceitos de Single Page Application (SPA), componentização e regras de negócio de anonimato e matching.

### Autor: **Michel Lavanere Sampaio**

## Fluxo de Interação do Usuário

Abaixo está o diagrama que ilustra o ciclo de vida do usuário na aplicação, mapeando as rotas de entrada, lógicas de controle de sessão e restrições de deleção baseadas no tipo de perfil (Candidato vs Empresa).

![Fluxograma do Usuário](src/assets/fluxo_01.svg)

## Tecnologias Utilizadas
* **TypeScript:** Tipagem estática e interfaces rigorosas (`models`).
* **Vite:** Bundler de alta performance.
* **Chart.js:** Visualização de dados (Gráficos de competências).
* **HTML5/CSS3:** Sem frameworks de estilo, utilizando CSS modular.
* **LocalStorage:** Persistência de dados e simulação de controle de sessão (`StorageService`).

## Arquitetura SOLID e Padrão MVC
O projeto foi totalmente refatorado para seguir os princípios SOLID, garantindo alto desacoplamento e facilitando a futura integração com APIs REST:

```text
frontend/
├── index.html                 # Ponto de entrada / Home
├── [outros .html]             # Templates de UI
└── src/
    ├── models/                # Interfaces de Entidades (IPessoa, ICandidato)
    ├── services/              # Camada de Infraestrutura e Persistência
    │   ├── interfaces/        # Contratos (ICrudService, IAuthService) - [ISP / OCP]
    │   └── *LocalStorage.ts   # Implementações concretas de serviços de Storage
    ├── controllers/           # Orquestração (Recebem Services via Injeção de Dependência) - [DIP]
    ├── validators/            # Regras isoladas (RegexValidator, DocumentValidator) - [SRP]
    └── pages/                 # Composition Root (Instancia os serviços e injeta nos Controllers)
 ```

## Funcionalidades
* **Autenticação Simulada:** Proteção de rotas baseada no tipo de usuário logado.
* **CRUD com Restrições:** Empresas apagam apenas suas vagas/contas; Candidatos apagam apenas seus perfis.
* **Anonimato e Tooltips:** Dados sensíveis ocultos nas listagens, com tooltips nativos (`title`) para resumos e detalhes.
* **Data Visualization:** Gráfico em tempo real demonstrando as competências mais procuradas.

## Como Executar
1. Certifique-se de ter o [Node.js](https://nodejs.org/) instalado.
2. Na raiz da pasta `frontend`, instale as dependências:
   \`npm install\`
3. Inicie o servidor de desenvolvimento:
   \`npm run dev\`