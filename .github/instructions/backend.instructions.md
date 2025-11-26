# Backend Instructions

Regras para o Copilot ao sugerir código Java/Spring Boot neste repositório.

## Arquitetura

- Usar **Use Cases** em vez de `*Service` genéricos.
  - Ex.: `CreateOrderUseCase`, `GetOrderUseCase`.
  - Uma ação por use case, método principal `execute(...)`.
- Depender de **abstrações**, não de implementações concretas.
  - Repositórios via interfaces Spring Data ou portas claras.
- Manter o **domínio rico**:
  - Regras de negócio em `Pedido`, `ItemDePedido`, etc.
  - Métodos de comportamento (`confirmar()`, `cancelar()`) em vez de ifs espalhados.

## Estilo de Código

- **Sem comentários** em código de produção e testes.
- Nomes claros e em português para métodos e variáveis.
- Métodos curtos, responsabilidade única; extrair métodos auxiliares quando necessário.
- Evitar `null` desnecessário; preferir tipos obrigatórios e validações antecipadas.

## Validação e Erros

- Validar entrada em bordas públicas (controllers, use cases, validators dedicados).
- Usar exceções específicas de domínio (ex.: `OrderNotFoundException`).
- Não lançar `RuntimeException` genérica.
- Não engolir exceções; logar com contexto e, se necessário, mapear para erro HTTP via `GlobalExceptionHandler`.

## Logging

- Usar `LogEvent` para logs importantes:
  - `ORDER_CREATED`, `ORDER_RETRIEVED`, `ORDER_NOT_FOUND`, `ORDER_UNEXPECTED_ERROR`, etc.
- Não logar dados sensíveis (tokens, senhas, cookies). Quando precisar, mascarar.

## Integração com Infra

- Ao usar repositórios JPA:
  - Entidades em `infrastructure.persistence.entity`.
  - Mapeamentos via `PedidoMapper`/MapStruct para converter entre domínio e entidade.
- Não acessar diretamente HTTP, banco ou outras infra do domínio; use portas/abstrações.

## Qualidade

- Respeitar Checkstyle e Sonar:
  - Ordem de imports via `CustomImportOrder` (java → terceiros → `com.poc.delivery.*`).
  - Evitar variáveis não usadas e código morto.
- Manter complexidade baixa; extrair pequenas funções quando o método ficar grande.
