# HIST-002 – Visualização de Pedidos pela Loja

## Descrição

"Como loja, quero visualizar os pedidos recebidos em tempo quase real para poder atendê-los rapidamente."

## Objetivo de Negócio

Permitir que a loja consulte os pedidos associados a ela, com filtros por status e data, de forma paginada.

## Critérios de Aceitação (alto nível)

- Deve ser possível listar pedidos de uma loja:
  - Filtrando por status (`CRIADO`, `EM_PREPARO`, `EM_ROTA`, `ENTREGUE`, `CANCELADO`).
  - Filtrando por intervalo de datas (criação do pedido).
  - Usando paginação (page/size/sort).
- Cada item da lista deve conter informações resumidas do pedido (id, status, valores principais, timestamps relevantes).

## Tasks Técnicas

- **T2.1 – Consulta de pedidos por loja (repositório)**
  - Implementar query/repositório para buscar pedidos por loja com filtros de status e data, paginados.

- **T2.2 – Endpoint REST de listagem de pedidos da loja**
  - Implementar `GET /api/v1/stores/{storeId}/orders` conforme `endpoints-rest-fase1.md`.
  - Implementar filtros e paginação utilizando a convenção definida na seção 1 do documento de endpoints.

- **T2.3 – Mapeamento para DTO de listagem**
  - Definir DTO de resposta resumida de pedido para listagem.
  - Utilizar MapStruct para mapear entidades/ domínio para o DTO.

- **T2.4 – Tratamento de erros**
  - Retornar `404 Not Found` se a loja não existir.
  - Tratar erros de parâmetros inválidos (datas, status) com `400 Bad Request`.

- **T2.5 – Testes**
  - Testes unitários (JUnit 6 + Instancio) para lógica de filtro e paginação (quando aplicável).
  - Testes de integração (Cucumber + Testcontainers + RestAssured) cobrindo cenários de listagem com filtros diferentes.
