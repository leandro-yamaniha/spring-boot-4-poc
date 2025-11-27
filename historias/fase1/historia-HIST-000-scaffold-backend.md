# HIST-000 – Criação do Scaffold do Backend

## Descrição

"Como desenvolvedor, quero criar o scaffold do backend Java/Spring Boot para que o time possa implementar as funcionalidades de pedidos de forma organizada e consistente com as diretrizes do projeto."

## Objetivo de Negócio/Técnico

Criar a base técnica do projeto backend, alinhada às decisões de stack e às boas práticas definidas em `planejamento/`, para suportar com segurança as histórias funcionais da Fase 1 (criação e visualização de pedidos).

## Critérios de Aceitação (alto nível)

- Projeto Gradle com **Java 25 LTS** e **Spring Boot 4.x** criado e versionado.
- Estrutura mínima de pacotes definida (api, application, domain, infrastructure).
- Profile **`local`** configurado com `application-local.yml`.
- Dependências principais adicionadas (Spring Web, Spring Data JPA, PostgreSQL, Redis, Actuator).
- Ferramentas de qualidade configuradas (Checkstyle, JaCoCo, JUnit 6, Testcontainers, MapStruct, Instancio).
- Projeto compila e sobe localmente (mesmo que com endpoints mínimos/health check).

## Tasks Técnicas

- **T0.1 – Criar projeto Spring Boot 4.x com Gradle**
  - Gerar projeto Spring Boot 4.x usando Gradle e Java 25 LTS.
  - Confirmar estrutura padrão de diretórios e versão do Spring Boot.

- **T0.2 – Definir estrutura de pacotes**
  - Criar pacotes base, por exemplo:
    - `...api` (controllers, DTOs).
    - `...application` (serviços de aplicação/casos de uso).
    - `...domain` (entidades, agregados, serviços de domínio).
    - `...infrastructure` (persistência, integrações externas, mappers para DB).

- **T0.3 – Configurar profile `local` e application-local.yml**
  - Definir `application-local.yml` com:
    - Porta HTTP da aplicação.
    - Configuração de datasource PostgreSQL (URL, usuário, senha).
    - Configuração de Redis.
    - Configurações básicas de logging.
  - Garantir que `local` é o profile utilizado na execução.

- **T0.4 – Adicionar dependências principais**
  - Spring Web, Spring Data JPA, PostgreSQL, Spring Cache/Redis, Actuator.
  - MapStruct (API + annotation processor).
  - JUnit 6, Testcontainers, RestAssured (se aplicável), Cucumber, Instancio.

- **T0.5 – Configurar ferramentas de qualidade**
  - Checkstyle (plugin Gradle + `checkstyle.xml`).
  - JaCoCo com meta de cobertura mínima de 90% para testes unitários.
  - Preparar integração com SonarQube/SonarCloud (relatórios de teste/cobertura exportáveis).

- **T0.6 – Health check básico**
  - Habilitar Actuator e garantir o endpoint `/actuator/health` funcional.
  - Adicionar teste simples de smoke test para verificar se a aplicação sobe com o profile `local`.

- **T0.7 – Integração com containers locais**
  - Garantir que a configuração `local` consegue conectar ao PostgreSQL/Redis definidos no `docker-compose` local (quando este for criado).

- **T0.8 – Documentação mínima**
  - Atualizar `README.md` com passos para rodar o projeto localmente (Gradle + profile `local`).
  - Referenciar os documentos de planejamento relevantes (`documento-executivo`, `requisitos-nao-funcionais-fase1`, `decisao-stack-http-fase1`, `tarefas-pre-implementacao-fase1`).
