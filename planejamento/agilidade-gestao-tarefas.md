# Gestão Ágil de Tarefas – Backend de Entrega de Pedidos

## 1. Objetivo

Estabelecer uma forma padrão de **planejar, acompanhar e finalizar tarefas** do projeto de backend de entregas, alinhada aos princípios do `agents.md`:

- TDD sempre que possível.
- Commits pequenos, focados e com testes.
- Lint e build passando.
- Transparência de status (o que está em andamento, bloqueado, pronto).

## 2. Ferramenta Recomendada
Como o projeto já está versionado em Git, a recomendação é usar uma ferramenta integrada ao repositório.

Para este projeto, será utilizado **Kanboard** (https://kanboard.org/) em ambiente local para gestão das tarefas.

- **Uso principal**: quadro Kanban local com colunas Backlog  To Do  In Progress  Code Review  Testing  Done.
- **Integração com Git**: os cartões podem referenciar branches/commits por ID de tarefa nas mensagens de commit, mesmo sem integração automática.

Se, no futuro, o repositório for hospedado em um provedor Git com boards nativos (GitHub/GitLab/Azure), o fluxo descrito aqui ainda se aplica, apenas trocando a ferramenta.

Independente da plataforma, o fluxo abaixo deve ser mantido.

## 3. Estrutura do Quadro (Kanban)

Colunas mínimas sugeridas:

- **Backlog**
  - Ideias, épicos e histórias ainda não priorizados para desenvolvimento.
- **To Do (Pronto para Iniciar)**
  - Itens priorizados, com escopo claro (Definition of Ready).
- **In Progress**
  - Tarefas em desenvolvimento; no máximo poucas por pessoa para evitar WIP excessivo.
- **Code Review**
  - Mudanças aguardando revisão de código (PR aberto).
- **Testing**
  - Mudanças em validação (testes manuais/integração em ambiente de teste).
- **Done**
  - Itens concluídos, com commit/merge em main e pipeline verde.

## 4. Tipos de Item

Recomenda-se usar ao menos estes tipos de item/cartão:

- **Épico**
  - Agrupa várias histórias de usuário relacionadas.
  - Ex.: "Fase 1 – MVP Core de Pedidos".
- **História de Usuário**
  - Descreve valor de negócio.
  - Ex.: "Como cliente, quero criar um pedido para receber produtos em casa".
- **Tarefa Técnica**
  - Atividade de implementação/infraestrutura.
  - Ex.: criar migration, configurar cache, adicionar métricas.
- **Bug**
  - Problema em funcionalidade já entregue.

## 5. Ligação com Fases do Roadmap

- Cada **Fase (1, 2, 3, 4)** do roadmap deve ser representada por **um ou mais épicos**.
- Histórias e tarefas técnicas devem ser vinculadas ao épico correspondente.
- Permite visualizar claramente o progresso de cada Fase/MVP.

## 6. Definition of Ready (DoR)

Um item só pode ir para **To Do** se tiver:

- **Descrição clara** do que precisa ser feito.
- **Critérios de aceitação** simples e objetivos.
- **Impactos conhecidos** (banco, APIs, contratos externos) identificados.
- **Dependências mapeadas** (outros times/serviços, decisões pendentes).

Itens sem essas informações permanecem em **Backlog** até serem refinados.

## 7. Definition of Done (DoD)

Um item só pode ir para **Done** se:

- Código implementado e revisado.
- **Testes automatizados relevantes** criados/atualizados e passando (unitários e, quando aplicável, integração).
- **Lint e build sem erros**.
- Documentação mínima atualizada (se houver mudança de comportamento observável):
  - README/CHANGELOG ou arquivos de planejamento em `planejamento/`.
- Deploy realizado no ambiente-alvo (dev/homolog/produção, conforme o escopo da tarefa).

## 8. Relação entre Tarefas, Branches e Commits

Sugestão de prática padrão:

- Cada item de quadro deve ter um **identificador** (ex.: `TCK-123`, `ISSUE-45`).
- Branches nomeadas com esse identificador:
  - Ex.: `feature/TCK-123-criacao-pedido`, `fix/ISSUE-45-ajuste-status`.
- Mensagens de commit no padrão **Conventional Commits**, incluindo o identificador:
  - Ex.: `feat: cria endpoint de criacao de pedido (TCK-123)`
  - Ex.: `fix: corrige calculo de valor_total do pedido (ISSUE-45)`

Isso facilita rastrear do quadro → PR → commit.

## 9. Uso Prático nas Próximas Etapas

Para continuar o projeto:

- Criar épico **"Fase 1 – MVP Core de Pedidos"**.
- Adicionar ao quadro, pelo menos:
  - História: "Criar e gerenciar pedidos".
  - História: "Cadastro e consulta de lojas e produtos".
  - Tarefa técnica: "Modelo de dados Fase 1" (já iniciado neste repositório).
  - Tarefa técnica: "Definição de endpoints REST da Fase 1".
- Mover estas tarefas pelo fluxo **Backlog → To Do → In Progress → Code Review → Testing → Done** conforme forem evoluindo.
