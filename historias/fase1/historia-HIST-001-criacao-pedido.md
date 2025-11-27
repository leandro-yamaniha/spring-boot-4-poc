# HIST-001 – Criação de Pedido

## Descrição

"Como cliente, quero criar um pedido para comprar produtos de uma loja."

## Objetivo de Negócio

Permitir que um cliente selecione uma loja, escolha produtos, informe endereço de entrega e forme um pedido persistido no sistema, com status inicial `CRIADO`.

## Critérios de Aceitação (alto nível)

- Deve ser possível criar um pedido informando:
  - Cliente válido.
  - Loja válida.
  - Endereço de entrega válido do cliente.
  - Lista de itens com produto, quantidade e observações.
- O sistema deve calcular o valor total do pedido (itens + taxa de entrega - descontos).
- O pedido deve ficar com status inicial `CRIADO`.
- Em caso de dados inválidos, o sistema deve retornar erro de validação (HTTP 400) com mensagem adequada.

## Tasks Técnicas

- **T1.1 – Modelo de domínio de Pedido**
  - Definir entidades de domínio para `Pedido` e `ItemDePedido` (pode ou não coincidir com entidades JPA).
  - Garantir que o modelo permita representar status, valores monetários e timestamps relevantes.

- **T1.2 – Entidades de persistência**
  - Mapear entidades JPA para pedido e itens, alinhadas ao modelo de dados da Fase 1.
  - Configurar relacionamentos (por exemplo, pedido 1:N itens).

- **T1.3 – Endpoint REST de criação de pedido**
  - Implementar `POST /api/v1/orders` conforme especificado em `endpoints-rest-fase1.md`.
  - Validar IDs de cliente, loja e endereço de entrega.
  - Validar itens (produto existente, quantidade > 0 etc.).

- **T1.4 – Regras de negócio de cálculo**
  - Implementar lógica de cálculo de total (somatório de itens, taxa de entrega, descontos).
  - Garantir testes unitários cobrindo cenários com/sem desconto, diferentes taxas de entrega etc.

- **T1.5 – Persistência do pedido**
  - Garantir que o pedido seja salvo no PostgreSQL com status inicial `CRIADO`.
  - Garantir integridade referencial com cliente, loja e endereço.

- **T1.6 – Tratamento de erros e respostas**
  - Usar `@ControllerAdvice/@RestControllerAdvice` para mapear exceções de domínio/validação para o padrão de erro JSON definido.
  - Retornar códigos HTTP apropriados (400/404/422) conforme o caso.

- **T1.7 – Testes**
  - Testes unitários (JUnit 6 + Instancio) para regras de negócio.
  - Testes de integração (Cucumber + Testcontainers + RestAssured) cobrindo fluxo de criação de pedido com sucesso e falhas de validação.
