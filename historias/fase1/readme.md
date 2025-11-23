# Fase 1 – MVP Core de Pedidos

Esta fase foca no **core de pedidos** do backend, cobrindo principalmente:

- Criação de pedido por clientes.
- Visualização de pedidos pelas lojas.
- Fluxo básico de status de pedido.

## Arquivos desta fase

- [Feature da Fase 1](./feature.md)
- [HIST-000 – Criação do scaffold do backend](./historia-HIST-000-scaffold-backend.md)
- [HIST-001 – Criação de pedido](./historia-HIST-001-criacao-pedido.md)
- [HIST-002 – Visualização de pedidos pela loja](./historia-HIST-002-visualizacao-pedidos-loja.md)

## Diagrama de Dependências entre Tasks

```mermaid
graph TD
  T01["T0.1 - Projeto Spring Boot 4.x + Gradle"]
  T02["T0.2 - Estrutura de pacotes (api, application, domain, infrastructure)"]
  T03["T0.3 - Profile local e application-local.yml"]
  T04["T0.4/T0.5 - Dependências e ferramentas de qualidade"]
  T06["T0.6/T0.7 - Health check e integração com containers locais"]

  T11["T1.1 - Modelo de domínio de Pedido"]
  T12["T1.2 - Entidades de persistência"]
  T13["T1.3 - Endpoint criação de pedido (POST /orders)"]
  T14["T1.4 - Regras de cálculo de total"]
  T15["T1.5 - Persistência do pedido"]
  T17["T1.7 - Testes HIST-001"]

  T21["T2.1 - Consulta de pedidos por loja (repositório)"]
  T22["T2.2 - Endpoint listagem de pedidos da loja (GET /stores/{storeId}/orders)"]
  T23["T2.3 - Mapeamento para DTO de listagem"]
  T25["T2.5 - Testes HIST-002"]

  T01 --> T02 --> T03 --> T04 --> T06 --> T11
  T11 --> T12 --> T13 --> T15
  T11 --> T14
  T15 --> T21 --> T22 --> T23

  T13 --> T17
  T22 --> T25
```

### Interpretação do Diagrama

- Tasks em **cadeia** (com setas) são dependentes.
  - Ex.: T12 depende de T11; T13 depende de T12; T21 depende de T15.
- Tasks **sem ligação direta** podem ser trabalhadas em paralelo, desde que suas dependências estejam satisfeitas.
  - Ex.: T14 (regras de cálculo) pode evoluir em paralelo a T21, desde que o modelo de domínio (T11) esteja definido.
  - Tests (T17, T25) podem ser iniciados à medida que os respectivos endpoints/regras vão sendo implementados.
