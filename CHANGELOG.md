# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Unreleased]

### Added

- **HIST-001: Implementação completa da criação de pedidos** 🎉
  - API REST com endpoint `POST /api/v1/orders`
  - Modelo de domínio rico (Pedido, ItemDePedido, StatusPedido)
  - Regras de negócio: cálculo de total, frete grátis, descontos
  - Arquitetura Clean Architecture (Controller → Use Case → Repository)
  - Validações completas e tratamento de erros padronizado
  - Persistência com JPA/Hibernate e Flyway migrations
  - Testes abrangentes (100% cobertura): unitários, integração, API
  - Quality Gate aprovado (SonarQube, Checkstyle, JaCoCo)

- **Arquitetura de domínio**
  - Classe `Pedido` com builder pattern e validações
  - Enum `StatusPedido` com estados do pedido
  - Classe `ItemDePedido` com cálculo automático de subtotal
  - Princípios SOLID aplicados (SRP, DIP, OCP)

- **API Layer**
  - `OrderController` com endpoint REST documentado
  - DTOs de request/response (PedidoRequest, PedidoResponse)
  - `GlobalExceptionHandler` com códigos de erro padronizados
  - Logging estruturado com `LogEvent` e códigos padronizados

- **Use Cases Layer**
  - `CreateOrderUseCase` - orquestração da criação de pedidos
  - `OrderValidator` - validações de negócio
  - `PriceCalculator` - cálculos de preço e desconto

- **Repository Layer**
  - `OrderRepository` - persistência de pedidos
  - Entidades JPA mapeadas corretamente
  - Relacionamentos e constraints implementados

- **Infraestrutura**
  - Configuração Flyway para migrations
  - Logging HTTP com `HttpRequestLoggingFilter`
  - Profiles de configuração (local, prod)
  - Docker Compose para ambiente de desenvolvimento

- **Qualidade e Testes**
  - Cobertura de 100% em código de negócio
  - Testes unitários para todas as regras de negócio
  - Testes de integração com Testcontainers
  - Testes de API com RestAssured
  - Checkstyle com 35+ regras aplicadas

### Issues Criadas para Melhorias Futuras

- **[HIST-002]** - Ajustar StatusPedido enum conforme documentação completa
- **[HIST-003]** - Implementar validações de existência de recursos
- **[HIST-004]** - Implementar controle de estoque e disponibilidade
- **[HIST-005]** - Melhorar observabilidade e métricas da API
- **[HIST-006]** - Implementar validações de segurança e rate limiting
- **[HIST-007]** - Otimizar performance e implementar cache
- **[HIST-008]** - Melhorar documentação e experiência do desenvolvedor
- **[HIST-009]** - Implementar internacionalização (i18n) e localização

### Fixed

- Corrigido Code Smell no teste contextLoads()
  - Adicionado comentário explicativo sobre o propósito do smoke test
  - Resolve issue S1186 do SonarQube (método vazio sem explicação)

### Removed

- Arquivos de build commitados por engano (app/build/, .gradle/)
- Artefatos compilados (.class, .jar)
- Relatórios de testes e checkstyle
- Cache do Gradle

### Added

- Arquivo .gitignore configurado para projetos Spring Boot com Gradle
  - Ignora diretórios de build e cache (.gradle/, build/)
  - Mantém Gradle Wrapper (essencial para builds reproduzíveis)
  - Ignora arquivos de IDEs (IntelliJ, Eclipse, VSCode)
  - Ignora arquivos temporários e de sistema operacional

- Script sonar-local.sh para análise SonarQube local com Docker
  - Inicia container SonarQube automaticamente
  - Configura Quality Gate "Zero Tolerance" (cobertura 100%, bugs 0, code smells 0)
  - Executa build e análise completa
  - Aguarda resultado do Quality Gate
  - Documentação completa em scripts/README.md

- Testes unitários para GlobalExceptionHandler (2 testes, 100% cobertura)

- Arquivo sonar-project.properties para integração com SonarQube
  - Exclusões alinhadas com JaCoCo (DeliveryApplication, common/logging)
  - Regras de qualidade alinhadas com Checkstyle (complexidade ≤10, métodos ≤50 linhas, etc)
  - Configuração de relatórios (JaCoCo XML, JUnit, Checkstyle)
  - Cobertura mínima configurada: 90%

- Regras de verificação de histórias e workflow de branches no agents.md
  - Item 4 na seção 10: "Verificar conclusão de histórias" (obrigatório a cada push)
  - Marcar histórias como concluídas quando critérios de aceitação forem atendidos
  - Atualizar status das features (em andamento, concluída, bloqueada)
  - Documentar decisões técnicas durante implementação
  - Atualizar planejamento e roadmap conforme necessário
  - Nova seção 10.1: "Regras para Branches e Histórias"
  - Nomenclatura de branches: feature/HIST-XXX-descricao ou fix/HIST-XXX-descricao
  - Commits devem referenciar história: feat(HIST-001): descrição
  - Pull Requests devem incluir: link história, critérios atendidos, testes
  - Merge apenas após aprovação e quality gates
  - Benefícios: rastreabilidade, histórico organizado, code review facilitado
- Arquivo LICENSE com licença MIT
  - Copyright (c) 2025 Leandro Yamaniha
  - Permissão para uso, modificação e distribuição
  - Atualizada seção de licença no README.md
- README.md na raiz do projeto
  - Visão geral completa do projeto
  - Badges de tecnologias e quality gate
  - Índice navegável
  - Instruções de instalação e execução
  - Documentação de qualidade de código (Checkstyle, JaCoCo, SonarQube)
  - Guia de testes e cobertura
  - Documentação da API (Swagger, OpenAPI, Actuator)
  - Estrutura do projeto
  - Regras de contribuição e commit
  - Links úteis e referências
- Regra obrigatória no agents.md: verificar e atualizar README.md antes de commit
  - Item 9 na seção "Regras Mínimas para Cada Commit"
  - Avaliar se mudanças impactam documentação do usuário
  - Atualizar README se houver novos recursos, comandos, configurações, etc
  - README deve sempre refletir o estado atual do código
  - Nunca commitar código sem documentação adequada
- Validação de 0 issues no script sonar-local.sh
  - Verifica issues não resolvidos após análise
  - Falha o build se houver qualquer issue (bugs, code smells, vulnerabilities)
  - Exibe contagem de issues encontrados
  - Adiciona "Issues: 0 ✓" no relatório de sucesso

### Changed

- Adicionada regra de atualização de versão na seção de push do agents.md
  - Seção 10 renomeada: "README, CHANGELOG e Versionamento"
  - Item 2 adicionado: "Atualizar Versão" (antes do CHANGELOG)
  - Ordem correta: 1.README → 2.Versão → 3.CHANGELOG → 4.Sincronia
  - Referência ao Versionamento Semântico da seção 8
  - Regras claras: feat→MINOR, fix→PATCH, BREAKING CHANGE→MAJOR
  - Locais de atualização: build.gradle e CHANGELOG.md
  - Versões de desenvolvimento com sufixo -SNAPSHOT
- Expandida seção de Objetivos de Negócio no README.md
  - Adicionada descrição da abordagem ágil e incremental
  - Enfatizado foco em MVP (Minimum Viable Product) em cada fase
  - Criada seção "Metodologia Ágil" com links para documentação
  - Links para planejamento/ (documento executivo, gestão ágil, ambiente)
  - Links para historias/ organizadas por fase (Fase 1-4)
  - Descrição de cada fase (MVP inicial, escalabilidade, integrações, analytics)
  - Princípios ágeis: MVP First, User Stories, Iterativo, Qualidade
- Atualizado .gitignore para ignorar arquivos do SonarQube
  - .scannerwork/ (diretório de trabalho do scanner)
  - .sonar/ (cache do SonarQube)
  - .sonar_lock (arquivo de lock)
  - **/report-task.txt (relatórios de análise)
- Atualizado script sonar-local.sh para usar SonarScanner CLI
  - Substituído plugin Gradle por SonarScanner CLI (compatibilidade Gradle 9.x)
  - Download e instalação automática do SonarScanner em ~/.sonar-scanner
  - Senha alterada para d3l1v3ry#Pr0j3ct
  - Melhorada verificação de Quality Gate existente
  - Adicionado timestamp ao nome do token gerado
  - Mensagens de sucesso mais detalhadas (cobertura, bugs, code smells)
- Adicionado plugin org.sonarqube ao build.gradle (versão 5.1.0.4882)
- Adicionada regra obrigatória no agents.md: executar análise SonarQube antes de commit
  - Executar ./scripts/sonar-local.sh antes de cada commit
  - Quality Gate "Zero Tolerance" deve passar (cobertura 100%, bugs 0, code smells 0)
  - Nunca commitar código que não passe no Quality Gate
  - Renumerada lista de regras (item 2 adicionado)
- Configurado JaCoCo para falhar build se cobertura < 95%
  - jacocoTestCoverageVerification com regra de 95% mínimo de linhas
  - Regra por classe: 90% mínimo
  - Exclusão de DeliveryApplication (entry point)
  - Verificação automática na task 'check' e 'build'
- Aumentada cobertura de testes de 66% para 100%
  - GlobalExceptionHandler: 0% → 100%
  - GlobalExceptionHandler.ApiError: 0% → 100%
  - GlobalExceptionHandler.ApiErrorResponse: 0% → 100%
- Configurado JaCoCo para excluir DeliveryApplication (entry point) da cobertura
- Configurado ImportOrder do Checkstyle para aceitar static imports no topo
  - Adicionado option="top" e sortStaticImportsAlphabetically="true"
- Configurado JaCoCo para gerar relatório automaticamente após testes
  - test.finalizedBy jacocoTestReport
  - Agora ./gradlew test gera o relatório HTML automaticamente
  - Não é mais necessário executar jacocoTestReport manualmente
- Atualizado README com instruções para visualizar relatórios de qualidade
  - Comandos para executar Checkstyle e JaCoCo
  - Como abrir relatórios HTML (main.html, index.html)
  - Comandos para macOS (open) e Linux (xdg-open)
  - Comando para verificar se relatório foi gerado
  - URL file:// para abrir diretamente no navegador
  - Descrição das 35 regras ativas do Checkstyle
  - Métricas disponíveis no JaCoCo (linhas, branches, métodos, classes)
- Adicionadas regras avançadas de Clean Code ao Checkstyle
  - **Complexidade:** Complexidade ciclomática máx 10, métodos máx 50 linhas, máx 5 parâmetros
  - **Tratamento de erros:** Proíbe catch vazio, proíbe exceções genéricas (Error, RuntimeException, Throwable)
  - **Evitar duplicação:** Detecta strings literais duplicadas (magic strings)
  - **Simplicidade:** Remove modificadores redundantes, imports não usados, imports com *
  - **Legibilidade:** Força @Override, uma declaração por linha, variáveis próximas ao uso
  - **Aninhamento:** Máx 3 níveis de if, máx 2 níveis de try
  - **Segurança (OWASP):** Proíbe System.out/err (usar logger)
  - **Tamanho:** Arquivos máx 500 linhas (Single Responsibility Principle)
- Adicionadas regras de nomenclatura (naming conventions) ao Checkstyle
  - Classes/Interfaces/Enums/Records: PascalCase (ex: MinhaClasse)
  - Métodos/Parâmetros/Variáveis/Atributos: camelCase (ex: meuMetodo)
  - Constantes (static final): UPPER_SNAKE_CASE (ex: MAX_VALUE)
  - Pacotes: lowercase com pontos (ex: com.poc.delivery)
  - Type parameters (generics): letra maiúscula única (ex: T, E, K, V)
  - Record components: camelCase (ex: nomeCompleto)
  - Mensagens de erro personalizadas em português com exemplos
- Configurado Checkstyle para falhar o build em caso de violações
  - maxWarnings = 0 (nenhum warning permitido)
  - ignoreFailures = false (não ignora falhas)
  - Checkstyle executa automaticamente antes dos testes
  - Checkstyle incluído na task 'check' e 'build'
- Adicionada regra de versionamento semântico no agents.md
  - Seguir Semantic Versioning 2.0.0 (MAJOR.MINOR.PATCH)
  - Atualizar versão conforme tipo de commit (Conventional Commits)
  - feat: incrementa MINOR, fix: incrementa PATCH, BREAKING CHANGE: incrementa MAJOR
  - Versões de desenvolvimento usam sufixo -SNAPSHOT
- Reforçada regra crítica no agents.md: build completo deve passar antes de qualquer commit
- Adicionado aviso explícito para executar `./gradlew clean build` antes de commitar

### Previously Added

- Scaffold inicial do backend Spring Boot 4.0.0 com Java 25
- Configuração do projeto Gradle multi-módulo
- SpringDoc OpenAPI 3.0.0 para documentação automática da API
- Spring Boot Actuator com todos endpoints expostos (profile local)
- Configuração de profiles (local) com application.yml e application-local.yml
- Checkstyle configurado para garantir qualidade de código
- JaCoCo configurado para cobertura de testes
- Estrutura de testes com JUnit 5, Testcontainers e Cucumber
- GlobalExceptionHandler para tratamento centralizado de erros
- OpenApiConfig para customização da documentação Swagger
- Estrutura de pacotes seguindo arquitetura em camadas (api, application, domain, infrastructure)
- Coleções Bruno para testes de API (Actuator e OpenAPI)
- Documentação de histórias de usuário (Fase 1 a 4)
- Documentação de planejamento técnico e arquitetural

### Stack Tecnológica

- Java 25 (LTS)
- Spring Boot 4.0.0
- Gradle 9.2.1
- Tomcat 11 (embedded)
- SpringDoc OpenAPI 3.0.0
- MapStruct 1.5.5
- Testcontainers 1.21.3
- Cucumber 7.14.0

### Endpoints Disponíveis

- `http://localhost:8080/swagger-ui.html` - Documentação Swagger UI
- `http://localhost:8080/v3/api-docs` - Especificação OpenAPI JSON
- `http://localhost:8080/actuator/*` - Endpoints do Spring Boot Actuator

---

## [0.0.1-SNAPSHOT] - 2025-11-23

### Inicial

- Inicialização do projeto (HIST-000)
