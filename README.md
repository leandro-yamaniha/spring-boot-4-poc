# Delivery Backend - Spring Boot 4 POC

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.2.1-blue.svg)](https://gradle.org/)
[![Quality Gate](https://img.shields.io/badge/Quality%20Gate-Zero%20Tolerance-success.svg)](http://localhost:9000)

Backend para plataforma de entrega de pedidos construído com Spring Boot 4, Java 25 e práticas rigorosas de qualidade de código.

## 🎯 Visão Geral

Este projeto é um Proof of Concept (POC) para um backend de entrega de pedidos, implementando:

- **✅ CRIADO DE PEDIDOS** - API completa para criação de pedidos com validações e cálculos
- **Cadastro de lojas, produtos, clientes e entregadores**
- **Criação, roteamento e acompanhamento de pedidos em tempo real**
- **Integração com meios de pagamento e serviços externos**
- **Padrões rigorosos de qualidade de código (Quality Gate "Zero Tolerance")**

### Status Atual - Fase 1 ✅ COMPLETA

**HIST-001: Criação de Pedido** - ✅ **IMPLEMENTADA E TESTADA**

- API REST completa com endpoint `POST /api/v1/orders`
- Modelo de domínio rico com validações e regras de negócio
- Cálculo automático de total (subtotal + frete - desconto)
- Persistência com PostgreSQL e Flyway
- Testes abrangentes (100% cobertura)
- Tratamento de erros padronizado
- Quality Gate aprovado

### Objetivos de Negócio

O projeto segue uma abordagem **ágil e incremental**, com foco em **MVP (Minimum Viable Product)** em cada fase:

- **Fase 1**: ✅ **CONCLUÍDA** - Viabilizar operação em uma região/cidade com fluxo completo de pedido → entrega
- **Fase 2+**: Escalar para múltiplas regiões, aumentar automação e robustez

### Metodologia Ágil

O desenvolvimento é organizado através de **Features** e **User Stories**:

- 📋 **Planejamento**: Documentação completa em [`planejamento/`](planejamento/)
  - [Documento Executivo](planejamento/documento-executivo-backend-entregas.md)
  - [Gestão Ágil de Tarefas](planejamento/agilidade-gestao-tarefas.md)
  - [Ambiente de Desenvolvimento](planejamento/ambiente-desenvolvimento-local.md)

- 📖 **Features e Histórias**: Organizadas por fase em [`historias/`](historias/)
  - [Fase 1](historias/fase1/) - ✅ **HIST-001 Concluída** (criação de pedidos, gestão básica)
  - [Fase 2](historias/fase2/) - Escalabilidade e automação
  - [Fase 3](historias/fase3/) - Integrações avançadas
  - [Fase 4](historias/fase4/) - Otimizações e analytics

**Princípios:**
- ✅ **MVP First** - Entregar valor incremental a cada fase
- ✅ **User Stories** - Foco em valor de negócio e experiência do usuário
- ✅ **Iterativo** - Feedback contínuo e ajustes rápidos
- ✅ **Qualidade** - Quality Gate rigoroso em cada entrega

## 🚀 Funcionalidades Implementadas

### ✅ Fase 1 - HIST-001: Criação de Pedido

#### **API REST**
- `POST /api/v1/orders` - Criar novo pedido
- Validação completa de entrada (cliente, loja, endereço, itens)
- Respostas padronizadas (201 Created / 400 Bad Request)
- Tratamento global de erros

#### **Modelo de Domínio**
- **Pedido**: Status, valores monetários, timestamps, itens
- **ItemDePedido**: Produto, quantidade, preço, subtotal, observações
- **StatusPedido**: CRIADO, CONFIRMADO, PRONTO, EM_ENTREGA, ENTREGUE, CANCELADO

#### **Regras de Negócio**
- **Cálculo de Total**: Subtotal (itens) + Taxa de Entrega - Desconto
- **Taxa de Entrega**: R$ 9,90 (frete grátis para pedidos ≥ R$ 100)
- **Desconto**: 5% para pedidos com ≥ 3 itens
- **Validações**: Cliente/loja/endereço/itens obrigatórios

#### **Arquitetura**
- **Clean Architecture**: Camadas bem definidas (Controller → Use Case → Repository)
- **SOLID Principles**: Single Responsibility, Dependency Inversion
- **Domain-Driven Design**: Regras de negócio no domínio

#### **Qualidade**
- **Cobertura**: 100% (excluindo infraestrutura)
- **Quality Gate**: Aprovado (SonarQube)
- **Checkstyle**: Zero violações
- **Testes**: Unitários + Integração + API

### 🎯 Roadmap de Melhorias

Issues criadas para próximas iterações:

| Issue | Funcionalidade | Prioridade | Status |
|-------|----------------|------------|---------|
| [HIST-004](#6) | Controle de Estoque | Alta | Planejada |
| [HIST-006](#8) | Segurança (Rate Limiting) | Alta | Planejada |
| [HIST-007](#9) | Performance e Cache | Alta | Planejada |
| [HIST-003](#5) | Validações de Existência | Média | Planejada |
| [HIST-005](#7) | Observabilidade | Média | Planejada |
| [HIST-002](#4) | Status Enum Completo | Baixa | Planejada |
| [HIST-008](#10) | Documentação Completa | Baixa | Planejada |
| [HIST-009](#11) | Internacionalização | Baixa | Planejada |

## 🔌 API REST

### Endpoint Principal

```http
POST /api/v1/orders
Content-Type: application/json

{
  "clienteId": "uuid",
  "lojaId": "uuid", 
  "enderecoId": "uuid",
  "itens": [
    {
      "produtoId": "uuid",
      "quantidade": 2,
      "precoUnitario": 25.00,
      "observacoes": "Sem cebola"
    }
  ]
}
```

### Resposta de Sucesso (201)

```json
{
  "id": "uuid-do-pedido",
  "clienteId": "uuid",
  "lojaId": "uuid",
  "enderecoId": "uuid",
  "itens": [
    {
      "id": "uuid-do-item",
      "produtoId": "uuid",
      "produtoNome": "Pizza Margherita",
      "quantidade": 2,
      "precoUnitario": 25.00,
      "subtotal": 50.00
    }
  ],
  "status": "CRIADO",
  "subtotal": 50.00,
  "taxaEntrega": 9.90,
  "desconto": 0.00,
  "total": 59.90,
  "createdAt": "2025-11-24T22:30:00Z",
  "updatedAt": "2025-11-24T22:30:00Z"
}
```

### Códigos de Erro

| Código | HTTP | Descrição |
|--------|------|-----------|
| `VALIDACAO_FALHOU` | 400 | Dados inválidos |
| `CLIENTE_NAO_ENCONTRADO` | 404 | Cliente não existe |
| `LOJA_NAO_ENCONTRADA` | 404 | Loja não existe |
| `ENDERECO_NAO_ENCONTRADO` | 404 | Endereço não existe |
| `INTERNAL_ERROR` | 500 | Erro interno |

### Exemplos de Uso

```bash
# Criar pedido
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d @examples/criar-pedido.json
```

Para mais exemplos, consulte [`collections/`](collections/) e [`historias/fase1/HIST-001-modelagem.md`](historias/fase1/HIST-001-modelagem.md).
  - [Fase 2](historias/fase2/) - Escalabilidade e automação
  - [Fase 3](historias/fase3/) - Integrações avançadas
  - [Fase 4](historias/fase4/) - Otimizações e analytics

**Princípios:**
- ✅ **MVP First** - Entregar valor incremental a cada fase
- ✅ **User Stories** - Foco em valor de negócio e experiência do usuário
- ✅ **Iterativo** - Feedback contínuo e ajustes rápidos
- ✅ **Qualidade** - Quality Gate rigoroso em cada entrega

## 🚀 Tecnologias

### Core

- **Java 25** - Linguagem de programação
- **Spring Boot 4.0.0** - Framework principal
- **Gradle 9.2.1** - Build e gerenciamento de dependências

### Qualidade de Código

- **Checkstyle 10.14.0** - 35+ regras de estilo e Clean Code
- **JaCoCo** - Cobertura de testes (100% obrigatório)
- **SonarQube** - Análise estática de código
- **SonarScanner CLI 6.2.1** - Integração com SonarQube

### Documentação

- **SpringDoc OpenAPI 3.0.0** - Documentação automática da API
- **Swagger UI** - Interface interativa para testes

### Testes

- **JUnit 6 (Jupiter)** - Framework de testes
- **AssertJ** - Assertions fluentes
- **Testcontainers** - Testes de integração com containers
- **Cucumber** - BDD (Behavior-Driven Development)
- **REST Assured** - Testes de API REST
- **Instancio** - Geração de dados de teste

## 📦 Pré-requisitos

- **Java 25** ou superior
- **Docker** (para SonarQube local)
- **Git**

## 🔧 Instalação

1. **Clone o repositório:**

```bash
git clone <repository-url>
cd spring-boot-4-poc
```

2. **Verifique a versão do Java:**

```bash
java -version
# Deve mostrar Java 25 ou superior
```

3. **Execute o build:**

```bash
./gradlew clean build
```

## ▶️ Executando o Projeto

### Desenvolvimento Local

```bash
./gradlew bootRun
```

A aplicação estará disponível em: **[http://localhost:8080](http://localhost:8080)**

### Com Profile Específico

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Executar Testes

```bash
# Testes unitários (padrão)
./gradlew test

# Somente testes de integração (@Tag("integration"))
./gradlew integrationTest

# Unitários + integração na mesma execução
./gradlew test integrationTest

# Com relatório de cobertura (unitários)
./gradlew test jacocoTestReport
```

Para detalhes sobre configuração do Gradle (tasks `test`/`integrationTest`, logs contínuos e Java toolchain), consulte [`GRADLE.md`](GRADLE.md).

## 🎯 Qualidade de Código

Este projeto implementa o **Quality Gate "Zero Tolerance"** com validações rigorosas:

### Métricas Obrigatórias

- **Cobertura de Testes:** 100%
- **Bugs:** 0
- **Code Smells:** 0
- **Issues:** 0
- **Checkstyle:** 0 violações

Para detalhes sobre o padrão de logging (códigos de evento, filtro HTTP e logs de domínio/erros), consulte [`LOGGING.md`](LOGGING.md).

### Executar Análise Completa

```bash
./scripts/sonar-local.sh
```

Este script irá:
1. ✅ Verificar Docker
2. 🐳 Iniciar SonarQube local
3. 🔑 Gerar token de autenticação
4. 🏗️ Executar build completo
5. 📊 Executar análise SonarQube
6. ✓ Validar Quality Gate

**Acesse o dashboard:** http://localhost:9000

### Checkstyle

Executar verificação:

```bash
./gradlew checkstyleMain checkstyleTest
```

Visualizar relatório:

```bash
open app/build/reports/checkstyle/main.html
```

### JaCoCo (Cobertura)

Gerar relatório:

```bash
./gradlew test jacocoTestReport
```

Visualizar relatório:

```bash
open app/build/reports/jacoco/test/html/index.html
```

## 🧪 Testes

### Estrutura de Testes

- **Testes Unitários:** `app/src/test/java`
- **Cobertura Atual:** 100%
- **Framework:** JUnit 6 + AssertJ

### Executar Testes

```bash
# Todos os testes
./gradlew test

# Testes de um módulo específico
./gradlew :app:test

# Com logs detalhados
./gradlew test --info
```

### Verificação de Cobertura

O build falha automaticamente se a cobertura for inferior a 95%:

```bash
./gradlew build
# Inclui: compile + checkstyle + test + jacocoTestCoverageVerification
```

## 📚 Documentação da API

### Swagger UI

Acesse a documentação interativa em:

**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### OpenAPI Spec

Especificação OpenAPI disponível em:

**[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)**

### Actuator

Endpoints de monitoramento:

- **Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Info:** [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)
- **Metrics:** [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

## 📁 Estrutura do Projeto

```bash
spring-boot-4-poc/
├── app/                          # Módulo principal da aplicação
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/poc/delivery/
│   │   │   │       ├── api/      # Controllers e configurações de API
│   │   │   │       └── DeliveryApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-local.yml
│   │   └── test/
│   │       └── java/
│   ├── config/
│   │   └── checkstyle/
│   │       └── checkstyle.xml    # 35+ regras de qualidade
│   └── build.gradle              # Configuração do módulo
├── scripts/
│   ├── sonar-local.sh           # Script de análise SonarQube
│   └── README.md                # Documentação dos scripts
├── planejamento/                # Documentação de planejamento
├── historias/                   # User stories e features
├── collections/                 # Coleções de API (Bruno, Insomnia)
├── agents.md                    # Regras para desenvolvimento
├── CHANGELOG.md                 # Histórico de mudanças
├── sonar-project.properties     # Configuração SonarQube
└── README.md                    # Este arquivo
```

## 🤝 Contribuindo

### Regras de Commit

Este projeto segue regras rigorosas definidas em [`agents.md`](agents.md):

1. ✅ **Build completo com sucesso** (`./gradlew clean build`)
2. 🔍 **Análise SonarQube** (`./scripts/sonar-local.sh`)
3. ✅ **Testes passando** (100% cobertura)
4. ✅ **Checkstyle sem violações**
5. 📝 **CHANGELOG atualizado**
6. 📚 **README atualizado** (se necessário)

### Conventional Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - Nova funcionalidade
- `fix:` - Correção de bug
- `docs:` - Mudanças na documentação
- `chore:` - Tarefas de manutenção
- `test:` - Adição ou correção de testes
- `refactor:` - Refatoração de código

### Versionamento Semântico

Seguimos [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** - Mudanças incompatíveis na API
- **MINOR** - Nova funcionalidade compatível
- **PATCH** - Correções de bugs compatíveis

## 📊 Quality Gates

### Checkstyle (35 regras)

**Nomenclatura:**
- Classes: PascalCase
- Métodos: camelCase
- Constantes: UPPER_SNAKE_CASE

**Complexidade:**
- Ciclomática ≤ 10
- Métodos ≤ 50 linhas
- Parâmetros ≤ 5
- Arquivos ≤ 500 linhas

**Clean Code:**
- Tratamento de erros explícito
- Sem duplicação de código
- Sem imports não usados
- Legibilidade e manutenibilidade

### SonarQube

**Quality Gate "Zero Tolerance":**
- Cobertura: 100%
- Bugs: 0
- Code Smells: 0
- Security Vulnerabilities: 0
- Security Hotspots: Revisados

## 🔗 Links Úteis

- [Documentação Spring Boot 4](https://docs.spring.io/spring-boot/docs/4.0.0/reference/html/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [SonarQube](https://www.sonarqube.org/)
- [Checkstyle](https://checkstyle.org/)
- [JaCoCo](https://www.jacoco.org/)

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

Copyright (c) 2025 Leandro Yamaniha

Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Desenvolvido com ❤️ usando Spring Boot 4 e práticas rigorosas de qualidade de código**
