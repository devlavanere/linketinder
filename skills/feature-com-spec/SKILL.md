# Skill: Feature com Spec-Driven Development (SDD) — Linketinder

## Objetivo
Implementar uma nova feature (funcionalidade) no projeto **Linketinder**, garantindo máxima previsibilidade e controle, através do fluxo contínuo: Especificação → Plano → Implementação Incremental.

## Entradas
- Descrição da feature desejada (regras de negócio ou caso de uso).
- Repositório do projeto Linketinder com o código fonte atual.
- `AGENTS.md` na raiz do projeto como referência de stack, estrutura, convenções e restrições.

## Estrutura de Specs

Cada feature possui sua própria pasta em `docs/specs/`:

```
docs/specs/
└── <nome-da-feature>/
    ├── spec.md          # Especificação da feature
    └── plano.md         # Plano de implementação com checklist
```

O `<nome-da-feature>` deve ser um identificador curto em `kebab-case` (ex: `notificacao-de-match`, `filtro-por-competencia`).

## Passo a Passo

1. **Etapa 1 (AGENTS.md):** Antes de iniciar, leia o `AGENTS.md` na raiz do projeto para compreender a stack tecnológica, estrutura, convenções de nomenclatura, padrões de teste (Spock) e, **sobretudo**, o Princípio do Anonimato (seção 5.3).

2. **Etapa 2 (Especificação):** Crie a pasta `docs/specs/<nome-da-feature>/` e o arquivo `spec.md`. Nele, defina:
   - **Contexto e Objetivo:** Por que a feature existe e qual problema resolve.
   - **Regras de Negócio:** Regras precisas e numeradas.
   - **Critérios de Aceite:** Cenários estruturados em `Dado/Quando/Então`.
   - **Impacto:** Quais componentes existentes serão afetados (modelos, DAOs, services, controllers, views, servlets, frontend).
   - **Princípio do Anonimato:** Declare explicitamente como a feature respeita o anonimato pré-match. Quais dados sensíveis (nome, email, CPF, CNPJ) são protegidos e quando são revelados.
   - **Fora do Escopo:** O que explicitamente NÃO será implementado.

3. **Etapa 3 (Plano de Implementação):** Crie o `plano.md` na mesma pasta. A especificação é quebrada em pequenas tarefas sequenciais. Cada tarefa indica:
   - O que será feito.
   - Quais arquivos serão tocados.
   - Como testar (teste Spock, verificação manual, ou ambos).
   - **Atualização do CHANGELOG.md:** Cada tarefa DEVE incluir a entrada correspondente no `CHANGELOG.md` na seção `[Não lançado]`, agrupada em `Adicionado`, `Modificado`, `Corrigido` ou `Removido`.

4. **Etapa 4 (Implementação Incremental):** Escreva o código estritamente necessário para uma tarefa do plano. Valide com testes Spock e/ou verificação manual. Marque `[X]` na tarefa, atualize o `CHANGELOG.md` e faça um *Commit Semântico*. Repita até zerar o plano.

5. **Etapa 5 (Validação Final):** Execute toda a suite de testes (`./gradlew test`), verifique o frontend (`npm run dev`) e valide o checklist de validação final.

## Checklist de Validação Final

Antes de considerar a feature concluída, verifique:

- [ ] Todos os critérios de aceite do `spec.md` foram atendidos.
- [ ] Todos os testes Spock relacionados passam (`./gradlew test`).
- [ ] O frontend compila e funciona sem erros (`cd frontend && npm run dev`).
- [ ] **Princípio do Anonimato:** Nenhum dado sensível (nome, email, CPF, CNPJ) é exposto antes do match.
  - [ ] Candidatos aparecem como "Candidato Anônimo" para empresas até o match.
  - [ ] Empresas aparecem como "🏢 Empresa Confidencial" para candidatos até o match.
  - [ ] Os endpoints da API filtram corretamente os dados sensíveis quando não há match.
- [ ] O `CHANGELOG.md` foi atualizado com todas as mudanças na seção `[Não lançado]`.
- [ ] O `AGENTS.md` foi atualizado, se a feature alterou a estrutura, stack ou convenções do projeto.
- [ ] Nenhuma funcionalidade fora do escopo foi modificada.

## Regras Absolutas
- **NENHUMA** linha de código deve ser escrita antes da aprovação total do `spec.md` e do `plano.md`.
- Uma tarefa do plano implementada por vez.
- Um commit atômico por tarefa implementada.
- **Cada tarefa DEVE atualizar o `CHANGELOG.md`** na seção `[Não lançado]` antes de ser marcada como concluída.
- **O Princípio do Anonimato é inegociável:** qualquer nova feature DEVE proteger os dados sensíveis (nome, email, CPF, CNPJ) até que ocorra o match. Consulte `AGENTS.md` seção 5.3.
- Não refatorar funcionalidades que estejam fora do escopo definido na Etapa 2.
- Seguir as convenções de nomenclatura do projeto (Groovy: `PascalCase`/`camelCase`; SQL: `snake_case`; TypeScript: `PascalCase`/`camelCase`).

## Modelos

### Modelo de `spec.md`

```markdown
# Especificação: <Nome da Feature>

## Contexto e Objetivo
[Descreva o problema e o objetivo da feature.]

## Regras de Negócio
1. [Regra 1]
2. [Regra 2]

## Critérios de Aceite

### Cenário 1: [Nome]
- **Dado** [condição inicial]
- **Quando** [ação]
- **Então** [resultado esperado]

## Impacto
- **Backend:** [quais arquivos/modelos/services serão alterados]
- **Frontend:** [quais arquivos/controllers serão alterados]
- **Banco de Dados:** [alterações no esquema, se houver]

## Princípio do Anonimato
[Declare como a feature respeita o anonimato pré-match.]

## Fora do Escopo
- [O que não será implementado]
```

### Modelo de `plano.md`

```markdown
# Plano de Implementação: <Nome da Feature>

- [ ] Tarefa 1: [Resumo]
  - **O que fazer:** [descrição]
  - **Onde alterar:** `[caminho/do/arquivo.groovy]`, `[caminho/do/arquivo.ts]`
  - **Como testar:** [teste Spock específico ou verificação manual]
  - **CHANGELOG.md:** [entrada prevista, ex: "Adicionado: novo endpoint para..."]

- [ ] Tarefa 2: [Resumo]
  - **O que fazer:** [descrição]
  - **Onde alterar:** `[caminho/do/arquivo]`
  - **Como testar:** [teste ou verificação]
  - **CHANGELOG.md:** [entrada prevista]
```

## Referências
- `AGENTS.md` — Guia de referência do projeto (stack, estrutura, convenções, restrições).
- `CHANGELOG.md` — Registro de mudanças do projeto (padrão Keep a Changelog).
- `database/` — Scripts SQL de inicialização e match.
- `src/test/groovy/linketinder/service/` — Testes Spock existentes como referência.