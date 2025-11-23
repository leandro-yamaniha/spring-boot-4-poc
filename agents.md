# Guia de Boas Práticas para Agents e Contribuidores

Este documento define regras mínimas para qualquer alteração automatizada ou manual no repositório, focando em:

- **TDD (Test-Driven Development)**
- **Clean Code**
- **Lint e formatação**
- **Padrões de segurança OWASP**
- **Testes unitários e de integração**
- **Regras mínimas para cada commit/push**
- **Atualização de README e CHANGELOG**

---

## 1. Princípios Gerais

- **Priorizar legibilidade sobre "esperteza"**  
  Código deve ser fácil de entender por qualquer pessoa do time.

- **Evitar duplicação de lógica**  
  Sempre que encontrar comportamento igual sendo repetido, extrair para função/módulo reutilizável.

- **Responsabilidades pequenas e bem definidas**  
  Funções, classes e módulos devem ter um único motivo claro para mudar.

- **Autonomia e segurança**  
  Toda automação (agents, scripts, pipelines) deve respeitar estas regras antes de escrever no repositório.

---

## 2. TDD – Test-Driven Development

- **Ciclo obrigatório para novas regras de negócio**:
  - **Red**: escrever um teste que falha representando o comportamento desejado.
  - **Green**: implementar o mínimo de código para fazer o teste passar.
  - **Refactor**: refatorar o código mantendo todos os testes passando.

- **Novos recursos**  
  Cada nova funcionalidade deve vir acompanhada de testes cobrindo os fluxos principais (felizes e de erro previsível).

- **Correção de bugs**  
  Antes de corrigir um bug, escrever um teste que reproduz o erro. Só então aplicar o fix.

- **Cobertura mínima (qualitativa)**  
  Mais importante do que porcentagem é garantir cobertura de **regras críticas**: autenticação, autorização, validações, integrações externas e cálculos sensíveis.

---

## 3. Clean Code

- **Nomes claros**  
  - Funções e métodos descrevem *o que fazem*.  
  - Variáveis descrevem *o que representam*.  
  - Evitar abreviações não óbvias.

- **Tamanho de funções e classes**  
  - Funções curtas, com um fluxo principal claro.
  - Classes/módulos agrupam comportamentos relacionados.

- **Tratamento de erros explícito**  
  - Não engolir exceções silenciosamente.
  - Propagar erros com contexto útil (mensagens claras) sem vazar informações sensíveis.

- **Comentários mínimos e úteis**  
  - Explicar apenas o *porquê* em casos não óbvios.  
  - Nunca usar comentários para encobrir código confuso: prefira refatorar.

---

## 4. Lint, Formatação e Qualidade Estática

- **Linter obrigatório antes de cada commit**  
  Sempre executar o(s) linter(s) configurado(s) no projeto (frontend, backend(s), scripts) antes de commitar.

- **Formatação automática**  
  Utilizar o formatador configurado (por exemplo, Prettier, ESLint + autofix, Spotless, etc.) quando disponível.

- **Build sem warnings graves**  
  Não ignorar avisos de compilador/linters que apontem possíveis bugs, problemas de segurança ou APIs obsoletas.

- **CI como guardião**  
  Pipelines de CI devem falhar se lint ou build falharem. Agents **não devem** tentar contornar essas falhas.

---

## 5. Segurança – Diretrizes OWASP

Toda contribuição deve respeitar, no mínimo, os princípios básicos do **OWASP Top 10**. Entre eles:

- **Validação e sanitização de entrada**  
  Nunca confiar em dados vindos do cliente ou de fontes externas. Validar tipos, tamanhos, formatos e valores permitidos.

- **Autenticação e autorização robustas**  
  - Proteger recursos sensíveis com autenticação.  
  - Verificar se o usuário tem permissão para acessar/modificar cada recurso.

- **Proteção contra injeção**  
  Utilizar APIs preparadas / parâmetros vinculados em vez de concatenar strings para consultas ou comandos externos.

- **Proteção de dados sensíveis**  
  - Nunca logar senhas, tokens, dados de cartão ou informações pessoais sensíveis.  
  - Utilizar armazenamento seguro para segredos (variáveis de ambiente, vault, etc.).

- **Manuseio seguro de erros**  
  - Mensagens para o usuário não devem revelar detalhes internos de stack, queries ou estrutura interna.  
  - Logs internos podem conter mais detalhes, mas sem expor dados sigilosos.

- **Dependências seguras**  
  - Manter bibliotecas e frameworks atualizados.  
  - Evitar dependências obsoletas ou sem manutenção conhecida.

---

## 6. Testes Unitários

- **Obrigatórios para lógica de negócio**  
  Toda função/método com regra de negócio relevante deve ter testes unitários cobrindo:
  - Fluxos de sucesso (happy path).
  - Fluxos de falha esperada (entradas inválidas, exceções de domínio).

- **Testes determinísticos**  
  - Não depender de horário real, rede externa ou ordem não determinística.  
  - Utilizar mocks/stubs/fakes quando necessário.

- **Critério de aceitação para merge**  
  Nenhum commit que altera código de produção deve ser mesclado sem testes unitários adequados.

---

## 7. Testes de Integração

- **Cobertura de fluxos entre componentes**  
  Escrever testes de integração para:
  - Comunicação entre módulos internos.  
  - Acesso a bancos de dados, filas, serviços externos (com ambientes de teste ou mocks realistas).  
  - Endpoints HTTP principais (controllers/APIs) e seus fluxos end-to-end no backend.

- **Ambiente isolado**  
  - Utilizar bancos de teste, containers ou perfis específicos de teste.  
  - Nunca apontar testes automatizados para ambientes de produção.

- **Reprodutibilidade**  
  - Testes devem poder ser executados localmente com instruções claras.  
  - Dados de teste devem ser criados e limpos pelo próprio teste.

---

## 8. Regras Mínimas para Cada Commit

Qualquer commit (manual ou criado por agents) **deve obedecer a todos os itens abaixo** **antes de ser criado**:

**⚠️ CRÍTICO: Executar `./gradlew clean build` (ou comando equivalente) e garantir sucesso ANTES de criar o commit.**

1. **Build completo com sucesso**
   - Executar build completo do projeto (`./gradlew clean build` ou equivalente).
   - Build deve completar sem erros (exit code 0).
   - Todos os módulos impactados devem compilar com sucesso.  
   - Aplicação (ou serviços afetados) devem iniciar sem erros em ambiente local ou de teste configurado.
   - **Nunca commitar código que não builda com sucesso.**

2. **Testes passando**
   - Testes unitários relevantes executam com sucesso.  
   - Testes de integração impactados pela mudança também passam.  
   - Não adicionar commits que quebrem o pipeline de CI.

3. **Lint sem erros**
   - Nenhum erro de linter pendente.  
   - Warnings relevantes avaliados e, se possível, eliminados.

4. **Escopo pequeno e coeso**
   - Cada commit deve focar em uma mudança lógica (feature, bugfix, refactor) bem definida.  
   - Evitar commits misturando refactor grande com mudança funcional.

5. **Mensagem de commit clara**
   - Descrever o que foi feito e, se relevante, o porquê.  
   - Se houver padrão de mensagem (ex.: Conventional Commits), segui-lo.

6. **CHANGELOG atualizado**
   - **Obrigatório**: Atualizar o CHANGELOG.md a cada commit que adiciona, modifica ou remove funcionalidades.
   - Adicionar entrada com data, tipo da mudança (Added/Changed/Fixed/Removed) e descrição concisa.
   - Manter formato consistente (sugestão: [Keep a Changelog](https://keepachangelog.com/)).
   - Commits de refactor interno sem impacto funcional podem omitir entrada no CHANGELOG.

7. **Versionamento Semântico**
   - Seguir [Semantic Versioning 2.0.0](https://semver.org/lang/pt-BR/) (MAJOR.MINOR.PATCH).
   - Atualizar versão conforme o tipo de commit (Conventional Commits):
     - **feat:** → incrementa MINOR (0.1.0 → 0.2.0)
     - **fix:** → incrementa PATCH (0.1.0 → 0.1.1)
     - **BREAKING CHANGE:** → incrementa MAJOR (0.1.0 → 1.0.0)
     - **chore:, docs:, style:, refactor:, test:** → não alteram versão
   - Atualizar versão em:
     - `build.gradle` (ou equivalente)
     - `CHANGELOG.md` (criar nova seção de versão ao fazer release)
   - Versões de desenvolvimento usam sufixo `-SNAPSHOT`

---

## 9. Regras para Push: README e CHANGELOG

Para **cada push** que altera comportamento observável da aplicação (features, endpoints, fluxos, contratos, performance relevante):

1. **Atualizar README**
   - Documentar novos recursos importantes.  
   - Atualizar seções de instalação, execução, endpoints, ambientes ou pré-requisitos, se forem impactados.

2. **Atualizar CHANGELOG**
   - Adicionar entrada descrevendo as mudanças mais relevantes desde a última versão/entrada.  
   - Seguir um formato consistente (por exemplo: data, tipo da mudança, breve descrição).

3. **Sincronia com código**
   - O que está no README e no CHANGELOG deve refletir o estado atual do repositório.  
   - Não deixar documentação desatualizada após merges significativos.

---

## 10. Regras Específicas para Agents Automatizados

Agents (como este) **devem SEMPRE**:

- **Respeitar todas as regras acima** antes de sugerir ou aplicar mudanças.
- **Evitar alterações grandes em lote** sem quebrar em passos menores e bem descritos.
- **Não criar arquivos desnecessários**; apenas o que for explicitamente solicitado ou justificado.
- **Descrever claramente** no sumário do que foi feito:
  - Arquivos alterados/criados/removidos.  
  - Razão principal da mudança.  
  - Impacto esperado (build, testes, performance, segurança).

Quando não for possível seguir alguma regra (por limitação de ambiente, tempo ou contexto), o agent deve:

- Explicar claramente qual regra não foi seguida e por quê.
- Sugerir próximos passos para que o time humano regularize a situação (ex.: criação de testes adicionais, ajuste de documentação, correção de warnings, etc.).

---

Este `agents.md` deve ser lido e seguido por qualquer pessoa ou automação que contribua com este repositório.
