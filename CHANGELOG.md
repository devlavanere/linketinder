# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adota [SemVer](https://semver.org/lang/pt-BR/) para versionamento.

## [Não lançado]

### Adicionado
- Tabela `recrutadores` no banco de dados — Feature Recrutador: permite que empresas cadastrem recrutadores vinculados para delegar curtidas de candidatos. Script: `database/linketinder_recrutador.sql`.
- Coluna `id_recrutador` na tabela `curtidas_empresas` — Rastreabilidade opcional: identifica qual recrutador realizou a curtida em nome da empresa. A coluna é nullable com `ON DELETE SET NULL`.
- Classe `Recrutador` (`linketinder.model.Recrutador`) — Model independente (não estende Pessoa) com atributos id, nome, email, cpf, senha, dataNascimento, areaRecrutamento, descricao, idEmpresa e método `getIdade()`.
- Classe `RecrutadorDTO` (`linketinder.model.RecrutadorDTO`) — Projeção segura sem dados sensíveis (exclui cpf, senha, dataNascimento). O método `toDTO()` em `Recrutador` converte o model para o DTO, garantindo o Princípio do Anonimato.
- Classe `RecrutadorDAO` (`linketinder.dao.RecrutadorDAO`) — Data Access Object com CRUD completo (inserir com `RETURN_GENERATED_KEYS`, listar, listarPorEmpresa, deletar) e verificações de unicidade global (`emailJaEmUso` cruzando candidatos, empresas e recrutadores; `cpfJaEmUso` cruzando candidatos e recrutadores).
- Classe `RecrutadorService` (`linketinder.service.RecrutadorService`) — Service de negócio com validações rigorosas: e-mail por Regex robusta, CPF com cálculo real de dígitos verificadores (Módulo 11) e rejeição de CPFs repetidos, data de nascimento não nula e não futura, idade ≥18 anos, senha forte (≥8 caracteres com maiúscula, minúscula, número e caractere especial) e unicidade global de e-mail e CPF via DAO.
- Testes Spock do `RecrutadorService` (`linketinder.service.RecrutadorServiceSpec`) — 13 testes BDD cobrindo cadastro válido, CPF inválido (tamanho, repetido, Módulo 11), e-mail sem '@', menor de 18 anos, data nula/futura, senha fraca (curta, sem maiúscula, sem número) e duplicação de e-mail/CPF via mock de `IRecrutadorDAO`.
- Classe `RecrutadorController` (`linketinder.controller.RecrutadorController`) — Controller com métodos `cadastrar`, `deletar` e `listarPorEmpresa`. O método `listarPorEmpresa` mapeia a lista de `Recrutador` para `RecrutadorDTO` usando `toDTO()`, garantindo que dados sensíveis (cpf, senha, dataNascimento) nunca sejam expostos pela API — Princípio do Anonimato.
- Classe `RecrutadorServlet` (`linketinder.api.RecrutadorServlet`) — Servlet REST mapeado em `/api/recrutadores` com endpoints: `POST` (cadastrar recrutador, retorna 201/400), `GET` (listar recrutadores por `idEmpresa`, retorna DTOs em JSON), `DELETE` (remover recrutador por `id`, retorna 204) e `OPTIONS` (CORS preflight, retorna 200). Headers CORS configurados com `Access-Control-Allow-Origin: *`.
- `AGENTS.md` — Guia de referência para agentes de IA com stack, estrutura, domínio, regras de negócio, Princípio do Anonimato, banco de dados, como executar, testar e convenções do projeto.
- `skills/feature-com-spec/SKILL.md` — Skill de Spec-Driven Development adaptado para o Linketinder, com specs organizadas em `docs/specs/<nome-da-feature>/`, checklist de validação incluindo o Princípio do Anonimato e obrigação de atualizar este CHANGELOG em cada tarefa.
- `CHANGELOG.md` — Este arquivo, seguindo o padrão Keep a Changelog.
- Testes Spock do `RecrutadorDTO` (`linketinder.model.RecrutadorDTOSpec`) — 3 testes BDD validando o Princípio do Anonimato: conversão `toDTO()` retorna apenas dados públicos (id, nome, areaRecrutamento, descricao, idEmpresa), verificação de que `RecrutadorDTO` não possui propriedades sensíveis (cpf, senha, dataNascimento) via reflexão, e serialização JSON via `JsonOutput` confirmando que as chaves "cpf", "senha" e "dataNascimento" não aparecem na string JSON resultante.

### Modificado
- Interface `IMatchDAO` (`linketinder.dao.interfaces.IMatchDAO`) — Adicionada assinatura `curtirCandidato(int idEmpresa, int idCandidato, Integer idRecrutador)` para suportar a rastreabilidade opcional do recrutador na curtida.
- Classe `CurtidaDAO` (`linketinder.dao.CurtidaDAO`) — Método `inserirCurtidaEmpresa` atualizado para incluir `id_recrutador` no INSERT SQL. Novo método `curtirCandidato(int, int, Integer)` implementado com `setObject` para suporte a nulos. O método antigo `curtirCandidato(int, int)` agora delega ao novo passando `null`, garantindo compatibilidade retroativa.
- Interface `IRecrutadorDAO` (`linketinder.dao.interfaces.IRecrutadorDAO`) — Adicionada assinatura `buscarPorId(Integer id)` para permitir que o MatchService localize o recrutador e extraia o `idEmpresa` antes de registrar a curtida.
- Classe `RecrutadorDAO` (`linketinder.dao.RecrutadorDAO`) — Implementado método `buscarPorId(Integer id)` com `PreparedStatement`; lança `RuntimeException` se o recrutador não for encontrado.
- Classe `MatchService` (`linketinder.service.MatchService`) — Adicionada dependência opcional `IRecrutadorDAO` via sobrecarga de construtores: o construtor original `MatchService(IMatchDAO)` permanece intacto para compatibilidade retroativa, e o novo `MatchService(IMatchDAO, IRecrutadorDAO)` habilita o fluxo de curtida por recrutador. Novo método `recrutadorCurteCandidato(int idRecrutador, int idCandidato)` valida IDs, busca o recrutador, extrai o `idEmpresa` e delega para `matchDao.curtirCandidato(idEmpresa, idCandidato, idRecrutador)`.
- Testes Spock do `MatchService` (`linketinder.service.MatchServiceSpec`) — Atualizados para cobrir o novo fluxo de recrutador: 5 testes BDD no total, incluindo recrutador curte candidato com `idRecrutador` repassado ao DAO (3 parâmetros), validação de IDs inválidos (zero/negativo) e verificação de RuntimeException quando `IRecrutadorDAO` não foi injetado (construtor antigo). Os testes existentes de candidato e empresa mantêm-se sem regressão.

### Corrigido

### Removido
