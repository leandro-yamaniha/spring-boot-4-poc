# Requisitos Não Funcionais – Fase 1 (MVP Core de Pedidos)

## 1. Disponibilidade, Desempenho e Capacidade

- **Disponibilidade (Fase 1)**
  - Meta: ~99% em horário comercial (ex.: 08h–23h).
  - Janelas planejadas de manutenção fora do horário de pico.

- **Latência de API (p95)**
  - `POST /orders`: ≤ 500 ms.
  - Endpoints de leitura (`GET /stores`, `GET /products`, `GET /orders/{id}`): ≤ 300 ms.

- **Volume e capacidade inicial**
  - Planejar para até 5k pedidos/dia em 1 cidade.
  - Pico esperado: até 20–30 requisições/segundo em horários de rush.

- **Timeouts**
  - Timeout de requisições externas (pagamento, notificação, etc.) configurável (por exemplo, 3–5s).
  - Timeout global de request HTTP (gateway/API) bem definido (por exemplo, 15s).

- **Otimizações futuras de startup**
  - Avaliar, em fases posteriores, o uso de **Spring AOT/native image** e de **CDS/AppCDS** do Java para reduzir tempo de inicialização e consumo de memória, caso se mostrem necessários para os objetivos de desempenho.

## 2. Segurança (OWASP – Recorte Mínimo)

- **Autenticação e Autorização**
  - Todo endpoint de escrita protegido (auth obrigatória).
  - Papéis mínimos (MVP): `CUSTOMER`, `STORE_USER`, `ADMIN/OPERATOR`.
  - Tokens e segredos não devem ser logados.

- **Transporte seguro**
  - Externo: HTTPS obrigatório em ambientes públicos (homolog/produção).
  - Interno: tráfego dentro de VPC; TLS para integrações externas sensíveis.

- **Validação de entrada**
  - Dados de entrada (body, query params, path vars) com validação de tipo, formato e tamanho máximo.
  - Proteção contra injeção:
    - Uso de ORM (JPA/Hibernate) ou queries parametrizadas.
    - Nunca concatenar input diretamente em SQL.

- **Proteção de dados sensíveis**
  - Não armazenar dados de cartão (usar tokenização do gateway).
  - Criptografia at rest delegada ao serviço gerenciado do banco (por exemplo, RDS com encryption on).
  - Segredos via variáveis de ambiente ou secret manager; nunca em código/commit.

- **Manuseio de erros**
  - Mensagens de erro para o cliente sem stack trace ou detalhes internos.
  - Logs internos com contexto suficiente, mas sem dados sensíveis (senhas, tokens, cartões).

## 3. Confiabilidade e Integridade de Dados

- **Transações**
  - Uso de transações ACID no banco (PostgreSQL) para:
    - Criação de pedido + itens.
    - Atualização de status.
    - Registro de pagamento vinculado ao pedido.

- **Idempotência**
  - Idempotência para:
    - Webhooks de pagamento (reprocessamentos).
    - Operações de criação de pedido em caso de reenvio pelo cliente/app (idempotency key opcional).

- **Migrations de banco**
  - Migrações versionadas (Flyway ou Liquibase).
  - Proibido alterar esquema manualmente em produção.

- **Backups e Restore**
  - Backups automáticos diários do banco (snapshots ou equivalente).
  - Política mínima de retenção (por exemplo, 7–14 dias em Fase 1).
  - Procedimento documentado de restore em ambiente de teste.

## 4. Observabilidade (Logs, Métricas, Health Checks)

- **Logs estruturados**
  - Formato JSON com campos como: `timestamp`, `level`, `logger`, `message`, `traceId`, `spanId`, `userId` (quando disponível), `orderId` (quando aplicável).
  - Níveis:
    - `INFO` para eventos de negócio chave (pedido criado, status alterado, pagamento aprovado/recusado).
    - `WARN`/`ERROR` para falhas e erros inesperados.
  - Nunca logar senhas, tokens de auth ou dados de cartão.

- **Métricas**
  - HTTP:
    - Contador de requisições por endpoint + status code.
    - Latência p95/p99 por endpoint.
  - Banco de dados:
    - Tempo médio de query (global), conexões em uso.
  - Negócio:
    - Número de pedidos criados por minuto/hora.
    - Distribuição de status (CRIADO, EM_PREPARO, EM_ROTA, ENTREGUE, CANCELADO).

- **Health checks**
  - `liveness` e `readiness` (por exemplo, com Spring Boot Actuator):
    - Liveness: processo vivo.
    - Readiness: conecta no banco, cache e serviços externos críticos dentro de limites configurados.

- **Tracing (evolução futura)**
  - Planejar integração com OpenTelemetry/Jaeger/Zipkin em fases seguintes.
  - Desde a Fase 1, prever `traceId` nos logs para futura correlação.

## 5. Escalabilidade e Arquitetura Operacional

- **Aplicação stateless**
  - Nenhum estado relevante em memória local (sessões, carrinho, etc.); uso de token (por exemplo, JWT) + banco/cache.

- **Uso de cache**
  - Redis para:
    - Catálogo de produtos e configurações de loja com TTL.
    - Leituras frequentes de pedidos recentes, conforme necessidade.

- **Escalabilidade inicial**
  - Escala vertical simples (aumentar CPU/memória).
  - Em cloud, prever scale-out horizontal (múltiplas instâncias atrás de load balancer).

- **Pool de conexões**
  - Pool de conexões de banco dimensionado para o número de instâncias.
  - Timeouts e limites configurados para evitar exaustão do banco.

## 6. Qualidade de Código, Testes e CI

- **TDD e testes**
  - Testes unitários obrigatórios para:
    - Regras de cálculo de valores do pedido.
    - Transições de status.
    - Integração com pagamentos (mocks/fakes).
  - Testes de integração para:
    - Criação de pedido end-to-end com banco de teste.
    - Atualização de status e seus efeitos.
    - Utilizar ferramentas como **Cucumber** (BDD), **Testcontainers** (infraestrutura efêmera para banco/serviços externos) e **RestAssured** (validação de APIs HTTP) quando fizer sentido para os cenários.
    - Adotar, em momento oportuno, uma ferramenta de relatório de testes que ofereça nível de detalhamento semelhante ao Serenity BDD (por exemplo, Allure, ReportPortal ou equivalente), integrando-a ao pipeline de CI para consolidar resultados de testes de aceitação/integrados.

- **Cobertura de testes (JaCoCo)**
  - Utilizar **JaCoCo** como ferramenta padrão de cobertura de código.
  - Estabelecer **cobertura mínima de 90%** para testes unitários dos módulos de regra de negócio, reforçada via configuração de build/CI (build falha se ficar abaixo do limiar definido).

- **Lint e formatação**
  - Execução de linter/formatador antes de commits.
  - Utilizar **Checkstyle** como ferramenta padrão de lint para código Java, com regras definidas no repositório (por exemplo, via plugin do Maven/Gradle).
  - Utilizar **SonarQube/SonarCloud** como ferramenta de análise estática e qualidade de código (quality gate), incluindo verificação de cobertura mínima (JaCoCo), code smells e vulnerabilidades.
  - CI deve falhar se o build quebrar, se testes falharem ou se o linter/análise estática (Checkstyle, Sonar e afins) apontar erros ou violação de qualidade gate.

- **Pipelines**
  - Ferramenta de build: **Gradle** (projeto configurado com Gradle como padrão, não Maven).
  - Pipeline mínimo: `gradle build`/`gradle check` (ou tarefas equivalentes) → (opcional) `docker build` antes de deploy.

## 7. Limites de Requisição e Resiliência

- **Rate limiting (nível de gateway/API)**
  - Limitar requisições por IP/cliente para endpoints sensíveis (por exemplo, criação de pedidos) para evitar abuso.

- **Fallbacks e retries**
  - Requisições a serviços externos (pagamentos, notificações) com:
    - Retry com backoff exponencial (limitado).
    - Número máximo de tentativas configurável por integração.

- **Circuit breaker**
  - Aplicar padrão de circuit breaker para integrações externas críticas (por exemplo, gateway de pagamento, serviço de notificações).
  - Comportamento esperado:
    - Abrir o circuito após um número configurável de falhas consecutivas em uma janela de tempo.
    - Permanecer aberto por um período de tempo configurável antes de permitir novas tentativas (half-open).
    - Registrar em log quando o circuito abrir/fechar para facilitar observabilidade e troubleshooting.
  - Em modo aberto, retornar erro controlado para o cliente e, se possível, orientar a tentar novamente mais tarde.

## 8. Plataforma de Execução (Java / Spring)

- **Versão de Java (LTS)**
  - Versão alvo: **Java 25 LTS**, adotada como base do projeto.
  - Dependências (Spring Boot, bibliotecas externas, plugins) devem ser escolhidas em versões compatíveis com Java 25 LTS.

- **Stack HTTP e modelo de concorrência**
  - Uso de **Spring MVC/Spring Web (não reativo)** rodando em **Spring Boot 4.x**, com **virtual threads** para lidar com I/O bloqueante (JDBC, HTTP externo) de forma escalável.
  - Para mais detalhes sobre a análise de prós e contras e a decisão tomada, ver:
    - [Decisão Arquitetural – Stack HTTP Fase 1](./decisao-stack-http-fase1.md)

- **Perfis Spring**
  - Podem existir perfis como `dev` e `prod` definidos na aplicação, mas **este projeto é focado em estudo e não terá deploy em cloud**.
  - Na prática, durante todo o ciclo deste projeto, **apenas o profile `local` será utilizado** para execução da aplicação.
  - Configurações específicas de ambiente (como endpoints externos) devem ser controladas por variáveis de ambiente/containers, mantendo o comportamento alinhado ao profile `local`.

## 9. Diretrizes de Código em Java

- **Uso de records**
  - Utilizar `record` para DTOs de entrada/saída de API, respostas de serviços e outros tipos de dados imutáveis onde fizer sentido.
  - Para entidades de persistência (por exemplo, JPA), avaliar caso a caso, priorizando compatibilidade com o framework antes de adotar records.

- **Não utilizar Lombok**
  - Não adicionar a dependência do Lombok neste projeto.
  - Preferir recursos nativos da linguagem (records, construtores explícitos, métodos auxiliares) e, quando necessário, geração de código pelo próprio IDE ou padrões simples de implementação.

- **Uso de MapStruct**
  - Utilizar **MapStruct** como ferramenta padrão para conversão/mapeamento entre:
    - DTOs de API ↔ modelos de domínio.
    - Modelos de domínio ↔ entidades de persistência (quando aplicável).
  - Evitar mapeamentos manuais extensos; centralizar lógica de mapeamento em interfaces `@Mapper` bem definidas, mantendo as regras de negócio fora das classes de mapeamento.

- **Uso de Instancio em testes**
  - Utilizar **Instancio** para criação de instâncias de objetos em testes automatizados, reduzindo boilerplate de montagem de dados.
  - Aplicar principalmente em testes de unidade e integração para gerar dados de entrada variados e focar a escrita do teste na lógica de negócio, e não na construção manual de objetos complexos.

- **Framework de testes – JUnit 6**
  - Utilizar **JUnit 6** como framework padrão para testes de unidade e integração.
  - Evitar misturar versões antigas (JUnit 4); novos testes devem seguir o modelo Jupiter (anotações `@Test`, `@Nested`, `@DisplayName`, etc.).

## 10. Ambiente de Desenvolvimento Local (Containers)

- **Docker/Docker Compose para ambiente local**
  - O ambiente de desenvolvimento local deve ser executável via **Docker/Docker Compose**, permitindo subir rapidamente os serviços necessários com um único comando.
  - Serviços mínimos previstos para o ambiente local:
    - Aplicação backend.
    - Banco de dados PostgreSQL.
    - Redis (cache).
  - O desenvolvedor deve poder escolher entre:
    - Rodar o backend localmente (IDE) apontando para containers de infraestrutura (DB/Redis).
    - Ou rodar também o backend em container, junto com os demais serviços, via Compose.
  - A configuração de containers deve espelhar, na medida do possível, o ambiente de produção (versões de banco, parâmetros principais), respeitando as diferenças entre ambientes.
