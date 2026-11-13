# Microsserviço de Cadastro (Experimento MOA)

Este diretório contém uma Prova de Conceito (PoC) desenvolvida para o desafio (K2-T3) da ZG-HERO. O objetivo é demonstrar a transição de um sistema monolítico para uma **Arquitetura Orientada a Microsserviços (MOA)**.

A responsabilidade de cadastrar Candidatos e Empresas foi extraída do fluxo principal e encapsulada neste microsserviço independente, que se comunica através de rede via requisições HTTP (REST).

## 1. O Backend (Groovy Nativo Sem Frameworks)

Como requisito do desafio, este servidor foi construído **sem o auxílio de frameworks** (como Spring Boot ou Micronaut).

- **Servidor:** Utilização da classe nativa `com.sun.net.httpserver.HttpServer` operando na porta `8080`.
- **Rotas (Endpoints):**
    - `POST /api/candidatos`
    - `POST /api/empresas`
- **Manipulação de Dados:** O payload recebido via rede é interceptado em `String` e convertido para objetos Groovy utilizando o `groovy.json.JsonSlurper`.
- **Integração de Banco (DIP):** O microsserviço reutiliza as regras de negócio do Linketinder instanciando os Controllers originais e injetando as dependências de acesso a dados (DAOs).
- **CORS:** Configuração manual de cabeçalhos (`Access-Control-Allow-Origin`) para permitir que o navegador (Frontend) consuma a API livremente.

## 2. Integração Exploratória com o Frontend (TypeScript)

Para comprovar o funcionamento "Full-Stack" da arquitetura, o Frontend em TypeScript foi refatorado utilizando o Princípio de Inversão de Dependência (DIP) do SOLID.

O salvamento em `LocalStorage` foi substituído por um novo Adapter (`CandidatoApiService`), responsável por:
1. **Limpeza de Dados:** Remoção de pontuações de CPF e CEP (`replace(/\D/g, '')`) usando Regex antes do envio.
2. **Formatação de Tipos:** Cálculo dinâmico da `dataNascimento` em padrão ISO (`YYYY-MM-DD`) a partir da idade informada no HTML, para satisfazer as regras de domínio do backend (`Fail-Fast`).
3. **Comunicação de Rede:** Uso nativo da API `fetch()` do JavaScript para empacotar o objeto em JSON e despachá-lo via método `POST`.

### Exemplo do Payload enviado pelo TS:
```json
{
  "nome": "Maria",
  "sobrenome": "Silva",
  "email": "maria@gmail.com",
  "cpf": "52611974659",
  "senha": "senha_padrao_frontend",
  "pais": "SP",
  "cep": "58025140",
  "descricao": "Entusiasta de programação.",
  "competencias": ["Java", "Groovy", "TypeScript"],
  "dataNascimento": "2001-01-01"
}
```

## Conclusão do Experimento

O experimento foi um sucesso. Os dados coletados pelo HTML viajaram pela rede em formato JSON, 
foram validados pelo servidor nativo Groovy e gravados com status 201 Created diretamente no PostgreSQL, 
provando a eficácia da comunicação distribuída entre sistemas independentes.

***Autor: Michel Lavanere Sampaio***

