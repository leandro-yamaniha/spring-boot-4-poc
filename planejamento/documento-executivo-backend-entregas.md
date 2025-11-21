# Documento Executivo – Backend de Entrega de Pedidos

## 1. Visão Geral e Objetivos

- **Propósito**  
  Construir um backend para uma plataforma de entrega de pedidos (ex.: restaurantes, mercados, farmácias) que permita:
  - Cadastro de lojas, produtos, clientes e entregadores.
  - Criação, roteamento e acompanhamento de pedidos em tempo real.
  - Integração com meios de pagamento e serviços externos (notificações, geolocalização, etc.).

- **Objetivos de negócio (curto/médio prazo)**  
  - **Fase 1**: Viabilizar operação em uma região/cidade com fluxo completo de pedido → entrega.  
  - **Fase 2+**: Escalar para múltiplas regiões, aumentar automação (atribuição de entregador, SLA, etc.) e robustez (disponibilidade, observabilidade, performance).

---

## 2. Escopo Funcional (Visão de Alto Nível)

### 2.1 Domínios Principais

- **Lojas/Restaurantes**
  - Cadastro e gestão de lojas.
  - Catálogo de produtos, preços e disponibilidade.
  - Horário de funcionamento, áreas de entrega, taxas.

- **Clientes**
  - Cadastro básico (nome, contato).
  - Endereços de entrega.
  - Histórico de pedidos.

- **Pedidos**
  - Criação de pedido (carrinho, validação de estoque e horários).
  - Status do pedido (criado → confirmado → em preparo → em rota → entregue/cancelado).
  - Cálculo de valor total (produtos, taxas de entrega, descontos/cupons).

- **Entregadores**
  - Cadastro de entregadores.
  - Associação de pedidos a entregadores.
  - Status do entregador (disponível, em rota, offline).

- **Pagamentos (nível executivo)**
  - Integração com gateway de pagamento (cartão, pix).
  - Status de pagamento associado ao pedido.
  - Estorno/ajustes básicos.

- **Notificações e Comunicação**
  - Notificações para cliente (pedido aceito, saiu para entrega, entregue).
  - Notificações para loja (novo pedido, cancelamento).
  - Canais: e-mail, push, SMS (priorizar 1–2 no MVP).

- **Administração & Operação**
  - Painel para suporte/operador interno acompanhar pedidos.
  - Visão de fila de pedidos por loja.
  - Ferramentas básicas de reprocesso (reenvio de notificação, reatribuição de entregador).

---

## 3. Requisitos Não Funcionais (Resumo)

- **Escalabilidade**: inicialmente para milhares de pedidos/dia, planejando crescimento para dezenas/centenas de milhares.  
- **Disponibilidade**: meta inicial ~99%, evoluindo para ≥99,5% nas fases posteriores.  
- **Performance**: APIs críticas (criar pedido, atualizar status) com latência p95 < 300–500 ms.  
- **Segurança**: autenticação/autorização robustas; criptografia em trânsito (HTTPS) e em repouso em serviços gerenciados.  
- **Observabilidade**: logs estruturados, métricas (requisições, erros, latência) e tracing para investigações.

---

## 4. Arquitetura de Alto Nível

- **Estilo arquitetural**
  - **Fase 1**: *Monólito modular* (por exemplo, um backend único com módulos de Pedido, Cliente, Loja, Entregador).
  - **Fases posteriores**: possibilidade de evoluir para microsserviços (Orders, Catalog, Payments, Delivery), à medida que escala exigir.

- **Interfaces**
  - **APIs REST** (JSON) para:
    - Apps/Frontends de cliente.
    - Painel administrativo.
    - Integrações B2B (parceiros, lojas integradas).
  - Webhooks/eventos para integrações com gateways de pagamento, notificações etc.

- **Comunicação interna**
  - Síncrona via HTTP/REST dentro da VPC.
  - Fases avançadas: assíncrona usando fila/stream (ex.: Kafka/RabbitMQ) para eventos de pedido (pedido_criado, pedido_em_rota, etc.).

---

## 5. Tecnologias – Banco de Dados, Cache e Cloud

### 5.1 Banco de Dados

- **Requisito principal**: consistência forte para transações de pedido/pagamento; consultas complexas (relatórios operacionais).  
- **Opções consideradas (resumo)**  
  - **Relacional (PostgreSQL, MySQL)**  
    - Pró: transações ACID, integridade referencial, SQL rico, ecossistema maduro.  
    - Contra: sharding manual em grande escala.
  - **NoSQL (Documentos, Key-Value)**  
    - Pró: escalabilidade horizontal mais simples, flexibilidade de schema.  
    - Contra: transações complexas e joins mais difíceis.

- **Recomendação executiva**
  - **PostgreSQL gerenciado** (ex.: AWS RDS / Azure Database for PostgreSQL / Cloud SQL Postgres).  
  - Modelo de dados normalizado para Pedidos, Itens, Clientes, Lojas, Entregadores, Pagamentos.

### 5.2 Cache

- **Objetivo**: reduzir carga no banco e melhorar latência para dados acessados frequentemente.  
- **Use cases de cache**
  - Catálogo de produtos e configurações de loja.
  - Sessões, tokens e dados de autenticação (se aplicável).
  - Estados de pedido em tempo quase real para telas de acompanhamento.

- **Tecnologia recomendada**
  - **Redis gerenciado** (ex.: AWS ElastiCache, Azure Cache for Redis, Memorystore).  
  - Padrões:
    - Cache-aside (aplicação lê do cache; em falha, lê do DB e popula).
    - TTLs curtas em dados que mudam com frequência.

### 5.3 Cloud / Infraestrutura

- **Cloud provider**  
  - Pode ser **AWS**, **Azure** ou **GCP**. Abaixo, exemplo com AWS (análogo nas demais):

- **Componentes principais**
  - **Compute**  
    - Fase 1: containers em ECS/Fargate ou EKS (Kubernetes gerenciado) – ou mesmo EC2 simples se time preferir.  
  - **Banco de Dados**  
    - AWS RDS PostgreSQL.  
  - **Cache**  
    - AWS ElastiCache for Redis.  
  - **Storage**  
    - S3 para arquivos (notas fiscais, comprovantes, imagens).  
  - **Rede e Segurança**
    - VPC, subnets públicas/privadas, Security Groups.
    - Load Balancer (ALB) ou API Gateway.  
  - **Autenticação/Autorização**
    - Cognito ou IDP externo (Auth0, Keycloak, etc.).  
  - **Observabilidade**
    - CloudWatch (logs e métricas) + stack adicional (ex.: Prometheus/Grafana, OpenTelemetry) conforme maturidade.

---

## 6. Stack de Backend (Sugerida)

(Alinhando com um cenário com Java/Spring, que combina bem com projetos corporativos.)

- **Linguagem & Framework**
  - Java + **Spring Boot** (REST, segurança, dados).
- **Banco de Dados**
  - PostgreSQL + ORM (Hibernate/JPA; Migrations via Flyway/Liquibase).
- **Cache**
  - Redis (integração com Spring Cache).
- **Mensageria (fases futuras)**
  - Kafka ou RabbitMQ para eventos assíncronos de pedidos.
- **Documentação de API**
  - OpenAPI/Swagger.
- **Testes**
  - TDD onde possível; testes unitários e de integração com banco em ambiente de teste.

---

## 7. Roadmap por Fases – Cada Fase como um MVP Completo

### Fase 1 – MVP Core de Pedidos (Cidade Única)

- **Objetivo**  
  Operar em produção em uma região/cidade com fluxo completo: cliente faz pedido → loja recebe → entregador entrega.

- **Escopo funcional**
  - Cadastro de lojas, clientes, catálogo básico.
  - Criação e gerenciamento de pedidos.
  - Status do pedido (do criado ao entregue/cancelado).
  - Integração com 1 gateway de pagamento (fluxo simples de pagamento aprovado/recusado).
  - Notificações básicas (e-mail ou push) para eventos de pedido.
  - Painel simples para lojas acompanharem pedidos.

- **Arquitetura**
  - Monólito modular em Spring Boot.
  - PostgreSQL + Redis.
  - Deploy em 1 região da cloud selecionada.
  - Logs, métricas básicas (requisições, erros).

- **Critérios de sucesso**
  - Sistema estável em produção com volume inicial (ex.: até 5k pedidos/dia).
  - Time conseguindo operar sem falhas graves em pedidos/pagamentos.

---

### Fase 2 – MVP Escalado com Entregadores e Operação

- **Objetivo**  
  Melhorar experiência de entrega e operações; suportar mais pedidos e mais lojas.

- **Escopo funcional**
  - Gestão completa de entregadores (cadastro, disponibilidade).
  - Atribuição de entregador a pedidos (manual + lógica automática básica).
  - Painel operacional interno para time de suporte acompanhar filas e intervenções.
  - Notificações em mais canais (ex.: SMS/push para entregador).
  - Otimizações de performance (uso mais agressivo de cache).

- **Arquitetura**
  - Possível extração do módulo de Entregas para um contexto mais isolado (ainda dentro de monólito ou como serviço separado, dependendo da maturidade).
  - Introdução de mensageria para eventos de pedido (pedido_criado, pedido_atualizado).
  - Hardening de segurança (rate limiting, melhoria de autenticação, papéis de usuário).

- **Critérios de sucesso**
  - Capacidade de atender múltiplas cidades/regiões.
  - Redução de incidentes operacionais; SLAs mínimos definidos (ex.: % de pedidos entregues dentro do prazo).

---

### Fase 3 – MVP Multi-Tenant e Marketplace Avançado

- **Objetivo**  
  Permitir expansão para múltiplas marcas/white-labels e verticalizar negócios.

- **Escopo funcional**
  - Suporte a múltiplos tenants (ex.: diferentes marcas ou parceiros rodando na mesma plataforma).
  - Regras de precificação mais avançadas (taxas dinâmicas, promoções complexas).
  - Integrações com parceiros externos (ERPs de grandes redes, sistemas de loja) via APIs/B2B.
  - Controles de acesso mais refinados (RBAC): dono da rede, gerente de loja, operador, etc.

- **Arquitetura**
  - Evoluir gradualmente para microsserviços onde fizer sentido (ex.: Orders, Catalog, Payments, Delivery).  
  - Estratégia de multi-tenancy (por schema, por coluna, ou por DB dedicado conforme cliente).

- **Critérios de sucesso**
  - Novos clientes/tenants podendo ser onboardados rapidamente.
  - Isolamento adequado entre tenants (segurança e performance).

---

### Fase 4 – Observabilidade, Analytics e Otimização

- **Objetivo**  
  Fornecer visão analítica e melhorar continuamente a operação.

- **Escopo funcional**
  - Pipelines de dados para data warehouse/lake (ex.: pedidos, entregas, tempos, rejeições).
  - Dashboards de desempenho (tempo médio de entrega, taxa de cancelamento, NPS, etc.).
  - Melhorias de rota/atribuição de entregadores usando dados históricos.
  - Otimizações de custo de infraestrutura e escalabilidade automática.

- **Arquitetura**
  - Streaming de eventos (Kafka/Kinesis/PubSub) para analytics.
  - Serviços auxiliares de BI/relatórios (ex.: ferramentas de visualização).

- **Critérios de sucesso**
  - Operação guiada por dados (decisões de expansão, promoções, SLAs).
  - Redução de custo por pedido mantendo ou melhorando SLAs.

---

## 8. Próximos Passos

- **1. Validar escopo das fases e prioridades de negócio**  
  Ajustar Fase 1 conforme restrições de prazo/orçamento (por exemplo, reduzir integrações externas inicialmente).

- **2. Decidir stack cloud e banco gerenciado**  
  Escolher AWS/Azure/GCP e serviços equivalentes (RDS, Redis, etc.).

- **3. Detalhar Fase 1 em épicos e histórias de usuário**  
  A partir dessa visão executiva, podemos descer para requisitos técnicos mais detalhados e começar a desenhar modelos de dados e contratos de API.
