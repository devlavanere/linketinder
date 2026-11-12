# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adota [SemVer](https://semver.org/lang/pt-BR/) para versionamento.

## [Não lançado]

### Adicionado
- Tabela `recrutadores` no banco de dados — Feature Recrutador: permite que empresas cadastrem recrutadores vinculados para delegar curtidas de candidatos. Script: `database/linketinder_recrutador.sql`.
- Coluna `id_recrutador` na tabela `curtidas_empresas` — Rastreabilidade opcional: identifica qual recrutador realizou a curtida em nome da empresa. A coluna é nullable com `ON DELETE SET NULL`.
- `AGENTS.md` — Guia de referência para agentes de IA com stack, estrutura, domínio, regras de negócio, Princípio do Anonimato, banco de dados, como executar, testar e convenções do projeto.
- `skills/feature-com-spec/SKILL.md` — Skill de Spec-Driven Development adaptado para o Linketinder, com specs organizadas em `docs/specs/<nome-da-feature>/`, checklist de validação incluindo o Princípio do Anonimato e obrigação de atualizar este CHANGELOG em cada tarefa.
- `CHANGELOG.md` — Este arquivo, seguindo o padrão Keep a Changelog.

### Modificado

### Corrigido

### Removido
