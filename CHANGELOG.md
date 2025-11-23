# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Unreleased]

### Added

- Arquivo .gitignore configurado para projetos Spring Boot com Gradle
  - Ignora diretórios de build e cache (.gradle/, build/)
  - Mantém Gradle Wrapper (essencial para builds reproduzíveis)
  - Ignora arquivos de IDEs (IntelliJ, Eclipse, VSCode)
  - Ignora arquivos temporários e de sistema operacional

### Removed

- Arquivos de build commitados por engano (app/build/, .gradle/)
- Artefatos compilados (.class, .jar)
- Relatórios de testes e checkstyle
- Cache do Gradle

### Changed

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
