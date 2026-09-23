<div align="center">
  <img src="assets/database.svg" alt="Modelo Conceitual brModelo" width="45%"/>
</div>

# Linketinder - Banco de Dados

Este diretório contém toda a documentação estrutural e os scripts de inicialização do banco de dados relacional (PostgreSQL) do projeto Linketinder.

**Desenvolvedor:** Michel Lavanere Sampaio

## Estrutura do Diretório

*   **`linketinder_init.sql`**: Script SQL principal contendo a DDL (criação das tabelas base) e a DML (inserção de dados mockados para testes).
*   **`linketinder_match.sql`**: Script SQL contendo as novas tabelas de curtidas e a query com a lógica de intersecção para gerar o Match.
*   **`assets/`**: Diretório contendo as imagens dos diagramas de modelagem (MER/DER).

## Modelagem e Normalização

O banco de dados foi projetado focando na integridade dos dados e na eliminação de redundâncias, passando pelas três primeiras Formas Normais (1FN, 2FN e 3FN):

1.  **Modelo Conceitual (brModelo):** Focado nas regras de negócio. Atributos compostos e multivalorados foram atomizados. A entidade `Competencias` foi isolada para permitir que candidatos e vagas se relacionem com ela sem duplicar informações.

<div align="center">
    <img src="assets/modelo_conceitual_01.png" alt="Modelo Conceitual brModelo" width="85%" />
</div>

2.  **Modelo Lógico/Físico (dbdiagram.io):** Tradução para o modelo relacional, onde as relações **N:N** (Candidato ↔ Competência e Vaga ↔ Competência) foram resolvidas com tabelas associativas, e a relação **1:N** (Empresa ↔ Vaga) foi amarrada via Chave Estrangeira (`id_empresa`).

<div align="center">
  <img src="assets/modelo_logico.png" alt="Modelo Lógico dbdiagram.io" width="85%" />
</div>

## A Lógica de Match (Caso de Uso 1)

O sistema de "Match" do Linketinder foi projetado de forma relacional, baseando-se na intersecção de dois eventos independentes (curtidas).

<div align="center">
  <img src="assets/modelo_logico_match.png" alt="Modelo Lógico - Sistema de Match" width="85%" />
</div>

### Como Funciona:
1. **O Evento do Candidato:** Quando um candidato (ex: Sandubinha) curte uma vaga de emprego, essa ação é registrada na tabela `curtidas_candidatos`, relacionando o `id_candidato` com o `id_vaga`.
2. **O Evento da Empresa:** De forma anônima e independente, quando uma empresa (ex: Pastelsoft) se interessa por um perfil técnico, essa ação é registrada na tabela `curtidas_empresas`, relacionando o `id_empresa` com o `id_candidato`.
3. **O Gatilho de Intersecção (Match):** A aplicação (representada pela query SQL relacional) realiza um rastreio cruzado. Ela verifica se:
   * O candidato *X* possui uma curtida na vaga *Y*.
   * A vaga *Y* pertence à empresa *Z*.
   * A empresa *Z* possui uma curtida no candidato *X*.
   
Se essa verificação triangular retornar verdadeira, o banco persiste esse encontro na tabela oficial de `matches`, liberando o canal de contato entre o recrutador e o talento. Essa lógica encontra-se consolidada no script `linketinder_match.sql`.

## Como Inicializar o Banco

1.  Certifique-se de ter o **PostgreSQL** instalado e o serviço em execução.
2.  Abra o seu client favorito (pgAdmin, DBeaver, IntelliJ Database Tool ou terminal `psql`).
3.  Crie um novo banco de dados vazio:
    ```sql
    CREATE DATABASE linketinder;
    ```
4.  Conecte-se ao banco `linketinder`.
5.  Execute o conteúdo do arquivo `linketinder_init.sql` para gerar a estrutura base.
6.  Em seguida, execute o arquivo `linketinder_match.sql` para gerar as tabelas de curtidas e testar a lógica do Match.

O script principal irá criar **6 tabelas** (candidatos, empresas, vagas, competencias, candidato_competencia e vaga_competencia) e popular o banco com **5 perfis de cada entidade** para que você possa realizar as consultas que quiser. O script secundário simulará o Caso de Uso de cruzamento de interesses.