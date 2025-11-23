# Tarefas Pré-Implementação – Fase 1 (MVP Core de Pedidos)

Este documento lista as principais tarefas a serem realizadas **antes de iniciar a implementação do backend** (código Java/Spring Boot), com base nos documentos de planejamento já criados.

## 1. Revisão de Planejamento

- [ ] Revisar o **Documento Executivo**:
  - Objetivo do MVP.
  - Escopo da Fase 1.
- [ ] Revisar o **Modelo de Dados – Fase 1** (`modelo-dados-fase1.md`).
- [ ] Revisar os **Requisitos Não Funcionais – Fase 1** (`requisitos-nao-funcionais-fase1.md`).
- [ ] Revisar a **Decisão de Stack HTTP** (`decisao-stack-http-fase1.md`).
- [ ] Revisar os **Endpoints REST – Fase 1** (`endpoints-rest-fase1.md`).
- [ ] Revisar o **Ambiente de Desenvolvimento Local** (`ambiente-desenvolvimento-local.md`).

Objetivo: garantir que o entendimento do domínio e das decisões técnicas esteja consolidado antes de escrever código.

---

## 2. Preparar Ambiente de Desenvolvimento

- [ ] Instalar e/ou validar instalação de:
  - [ ] **Java 25 LTS**.
  - [ ] **Docker** e **Docker Compose**.
  - [ ] **Gradle**.
  - [ ] IDE (IntelliJ IDEA ou equivalente) com suporte a Java 25.
- [ ] Configurar acesso ao repositório Git deste projeto.
- [ ] (Opcional) Configurar plugins na IDE para:
  - [ ] Suporte a **Checkstyle**.
  - [ ] Suporte a **SonarLint** (ou plugin equivalente para análise local).

---

## 3. Inicializar Projeto Spring Boot 4 + Gradle

- [ ] Criar projeto **Spring Boot 4.x** com:
  - [ ] Linguagem: **Java 25 LTS**.
  - [ ] Build: **Gradle**.
  - [ ] Dependências iniciais mínimas:
    - Spring Web (MVC).
    - Spring Data JPA.
    - Driver PostgreSQL.
    - Spring Cache (para Redis).
    - Spring Boot Actuator.
- [ ] Definir **estrutura de pacotes** (exemplo):
  - `...application` (casos de uso/serviços de aplicação).
  - `...domain` (entidades de domínio, agregados, serviços de domínio).
  - `...infrastructure` (adapters de persistência, HTTP clients, mappers para DB).
  - `...api` (controllers REST, DTOs de request/response).
- [ ] Configurar **profile `local`** como padrão de execução.

---

## 4. Configurar Arquivos de Configuração

- [ ] Criar `application-local.yml` com, pelo menos:
  - [ ] Configuração de porta HTTP.
  - [ ] Configuração de datasource PostgreSQL (URL, usuário, senha) para ambiente local/container.
  - [ ] Configuração de Redis.
  - [ ] Configurações básicas de logging.
- [ ] Garantir que o **profile `local`** é o único utilizado na prática, conforme diretriz (outros podem existir apenas conceitualmente).

---

## 5. Configurar Ferramentas de Qualidade

- [ ] Adicionar e configurar **Checkstyle** via Gradle:
  - [ ] Arquivo de regras `checkstyle.xml` no repositório.
  - [ ] Tarefa Gradle integrada ao `check`.
- [ ] Adicionar e configurar **JaCoCo**:
  - [ ] Geração de relatório de cobertura.
  - [ ] Definir **cobertura mínima de 90%** para módulos com regra de negócio (ao menos como target inicial do projeto).
- [ ] Preparar integração futura com **SonarQube/SonarCloud**:
  - [ ] Garantir que o build exporta relatórios de teste e cobertura (JaCoCo) em formatos compatíveis.

---

## 6. Configurar Dependências de Testes

- [ ] Adicionar dependências de testes no Gradle:
  - [ ] **JUnit 5 (Jupiter)**.
  - [ ] **Testcontainers** (PostgreSQL, Redis e outros necessários).
  - [ ] **Cucumber** (para testes BDD de integração, quando for utilizado).
  - [ ] **RestAssured** (para testes de API HTTP, quando fizer sentido).
  - [ ] **Instancio** (para criação de instâncias em testes).
- [ ] Verificar alinhamento das versões das bibliotecas com **Java 25 LTS** e **Spring Boot 4.x**.

---

## 7. Configurar Mapeamento e DTOs

- [ ] Adicionar dependências do **MapStruct** (API + annotation processor) no Gradle.
- [ ] Definir convenção de pacotes para mapeadores (por exemplo, `...api.mapper`, `...infrastructure.mapper`).
- [ ] Planejar os principais mapeamentos:
  - [ ] DTOs de API ↔ modelos de domínio.
  - [ ] Modelos de domínio ↔ entidades de persistência (quando aplicável).
- [ ] Garantir que as entidades/DTOs que forem imutáveis sejam modeladas como **records**, conforme diretriz.

---

## 8. Preparar Ambiente Local com Containers

- [ ] Definir (ou revisar) o `docker-compose.yml` do backend com, no mínimo:
  - [ ] Serviço `backend-app` (a ser conectado ao build Gradle/Dockerfile).
  - [ ] Serviço `postgres` (banco de dados local da aplicação).
  - [ ] Serviço `redis` (cache).
- [ ] Validar que a aplicação consegue:
  - [ ] Rodar localmente na IDE conectando-se aos containers de infraestrutura.
  - [ ] Ser executada em container, quando desejado, junto com os demais serviços.

---

## 9. Organizar Testes Iniciais (TDD)

- [ ] Definir o primeiro conjunto de **casos de teste de negócio** para o domínio de pedidos (ex.: criação de pedido, mudança de status).
- [ ] Criar **esqueleto de teste unitário** com JUnit 5 para pelo menos um caso crítico (por exemplo, cálculo de total do pedido com taxa de entrega e desconto).
- [ ] Criar **esqueleto de teste de integração** (Cucumber + Testcontainers + RestAssured) para um fluxo simples de criação e consulta de pedido (mesmo que ainda não implementado).

---

## 10. Atualizar README e Documentação

- [ ] Atualizar o `README.md` (ou criar, se ainda não existir) com:
  - [ ] Requisitos de ambiente (Java, Docker, Gradle).
  - [ ] Como rodar testes (`gradle test`, `gradle check`).
  - [ ] Como subir o ambiente local com Docker Compose.
  - [ ] Links para os documentos de planejamento em `planejamento/`.
- [ ] Garantir que o planejamento (`documento-executivo`, `modelo-dados`, `requisitos-nao-funcionais`, `decisao-stack-http`, `endpoints-rest-fase1`, `ambiente-desenvolvimento-local`, este arquivo) está referenciado de forma clara.
