# Endpoints REST – Fase 1 (MVP Core de Pedidos)

## 1. Contexto e Convenções

- **Base URL sugerida**: `/api/v1`
- **Formato**: JSON
- **Autenticação**: a ser detalhada em documento específico, mas já considerar header `Authorization: Bearer <token>`.
- **Erros**: resposta padrão de erro de domínio/validação:

  ```json
  {
    "error": {
      "code": "ORDER_NOT_FOUND",
      "message": "Pedido não encontrado"
    }
  }
  ```

  - **HTTP status recomendados**:
    - `400 Bad Request`: erros de validação de entrada.
    - `401 Unauthorized`: ausência/falha de autenticação.
    - `403 Forbidden`: usuário autenticado, mas sem permissão.
    - `404 Not Found`: recurso não encontrado (pedido, loja, cliente, etc.).
    - `409 Conflict`: conflitos de estado (ex.: tentativa de alterar pedido já entregue).
    - `422 Unprocessable Entity`: regras de negócio violadas (quando fizer sentido separar de 400).
    - `500 Internal Server Error`: erros inesperados.

- **Paginação** (quando aplicável):
  - Query params padrões: `page`, `size`, `sort`.
  - Resposta paginada (exemplo simplificado):

    ```json
    {
      "content": [ /* items */ ],
      "page": 0,
      "size": 20,
      "totalElements": 120,
      "totalPages": 6
    }
    ```

---

## 2. Lojas (Stores)

### 2.1. Listar lojas

- **GET** `/api/v1/stores`
- **Descrição**: lista lojas disponíveis para receber pedidos.
- **Query params (opcionais)**:
  - `city`: filtrar por cidade.
  - `state`: filtrar por estado.
  - `active`: filtrar por ativo/inativo (true/false).
- **Resposta 200** (exemplo):

  ```json
  [
    {
      "id": "uuid-loja",
      "name": "Pizzaria da Praça",
      "businessType": "RESTAURANTE",
      "city": "São Paulo",
      "state": "SP",
      "phone": "11999990000",
      "active": true
    }
  ]
  ```

### 2.2. Criar loja

- **POST** `/api/v1/stores`
- **Descrição**: cria uma nova loja.
- **Request body**:

  ```json
  {
    "name": "Pizzaria da Praça",
    "document": "12345678000100",
    "businessType": "RESTAURANTE",
    "email": "contato@pizzaria.com",
    "phone": "11999990000",
    "city": "São Paulo",
    "state": "SP"
  }
  ```

- **Respostas**:
  - `201 Created` com representação da loja criada.
  - `400 Bad Request` para validações de entrada.

### 2.3. Detalhar loja

- **GET** `/api/v1/stores/{storeId}`
- **Respostas**:
  - `200 OK` com dados da loja.
  - `404 Not Found` se loja inexistente.

### 2.4. Atualizar loja

- **PUT** `/api/v1/stores/{storeId}`
- **Descrição**: atualiza dados principais da loja.

### 2.5. Ativar/Desativar loja

- **PATCH** `/api/v1/stores/{storeId}/status`
- **Request body**:

  ```json
  { "active": true }
  ```

---

## 3. Produtos (Products)

### 3.1. Listar produtos de uma loja

- **GET** `/api/v1/stores/{storeId}/products`
- **Query params (opcionais)**:
  - `active`
  - `categoryId`

### 3.2. Criar produto

- **POST** `/api/v1/stores/{storeId}/products`
- **Request body**:

  ```json
  {
    "name": "Pizza Margherita",
    "description": "Queijo, tomate e manjericão",
    "price": 39.90,
    "categoryId": "uuid-categoria",
    "active": true
  }
  ```

### 3.3. Detalhar produto

- **GET** `/api/v1/stores/{storeId}/products/{productId}`

### 3.4. Atualizar produto

- **PUT** `/api/v1/stores/{storeId}/products/{productId}`

### 3.5. Ativar/Desativar produto

- **PATCH** `/api/v1/stores/{storeId}/products/{productId}/status`

- **Request body**:

  ```json
  { "active": false }
  ```

---

## 4. Clientes e Endereços

### 4.1. Criar cliente

- **POST** `/api/v1/customers`
- **Request body**:

  ```json
  {
    "name": "João Silva",
    "email": "joao@example.com",
    "phone": "11988887777"
  }
  ```

### 4.2. Detalhar cliente

- **GET** `/api/v1/customers/{customerId}`

### 4.3. Listar endereços do cliente

- **GET** `/api/v1/customers/{customerId}/addresses`

### 4.4. Criar endereço para cliente

- **POST** `/api/v1/customers/{customerId}/addresses`

- **Request body**:

  ```json
  {
    "alias": "Casa",
    "street": "Rua A",
    "number": "123",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01000-000",
    "principal": true
  }
  ```

---

## 5. Pedidos (Orders)

### 5.1. Criar pedido

- **POST** `/api/v1/orders`
- **Descrição**: cria um pedido associando cliente, loja, endereço e itens.

- **Request body (MVP simplificado)**:

  ```json
  {
    "customerId": "uuid-cliente",
    "storeId": "uuid-loja",
    "deliveryAddressId": "uuid-endereco-cliente",
    "items": [
      {
        "productId": "uuid-produto",
        "quantity": 2,
        "notes": "sem cebola"
      }
    ],
    "deliveryFee": 8.50,
    "discount": 0,
    "paymentMethod": "PIX",
    "observations": "tocar o interfone"
  }
  ```

- **Respostas**:
  - `201 Created` com resumo do pedido:

    ```json
    {
      "id": "uuid-pedido",
      "status": "CRIADO",
      "totalAmount": 88.30
    }
    ```
  - `400 Bad Request` para erros de validação.
  - `404 Not Found` se `customerId`, `storeId` ou `deliveryAddressId` inexistentes.

### 5.2. Detalhar pedido

- **GET** `/api/v1/orders/{orderId}`
- **Resposta 200** (exemplo simplificado):

  ```json
  {
    "id": "uuid-pedido",
    "customerId": "uuid-cliente",
    "storeId": "uuid-loja",
    "status": "EM_PREPARO",
    "items": [
      {
        "productId": "uuid-produto",
        "productName": "Pizza Margherita",
        "unitPrice": 39.90,
        "quantity": 2,
        "notes": "sem cebola"
      }
    ],
    "deliveryFee": 8.50,
    "discount": 0,
    "totalAmount": 88.30,
    "paymentMethod": "PIX",
    "paymentStatus": "APROVADO",
    "createdAt": "2025-01-01T12:00:00Z",
    "deliveredAt": null
  }
  ```

### 5.3. Listar pedidos de um cliente

- **GET** `/api/v1/customers/{customerId}/orders`
- Suporta paginação.

### 5.4. Listar pedidos de uma loja

- **GET** `/api/v1/stores/{storeId}/orders`
- **Query params (opcionais)**:
  - `status`
  - `dateFrom`, `dateTo`
  - paginação

### 5.5. Atualizar status do pedido

- **PATCH** `/api/v1/orders/{orderId}/status`

- **Request body**:

  ```json
  { "status": "EM_PREPARO" }
  ```

- Status válidos:
  - `CRIADO`, `CONFIRMADO`, `EM_PREPARO`, `EM_ROTA`, `ENTREGUE`, `CANCELADO`.

### 5.6. Cancelar pedido

- **POST** `/api/v1/orders/{orderId}/cancel`

- **Request body**:

  ```json
  { "reason": "Cliente solicitou cancelamento" }
  ```

---

## 6. Entregadores (Couriers)

### 6.1. Cadastrar entregador

- **POST** `/api/v1/couriers`

- **Request body**:

  ```json
  {
    "name": "José Motoboy",
    "type": "PARCEIRO",
    "phone": "11977776666",
    "vehiclePlate": "ABC1D23"
  }
  ```

### 6.2. Detalhar entregador

- **GET** `/api/v1/couriers/{courierId}`

### 6.3. Listar pedidos de um entregador

- **GET** `/api/v1/couriers/{courierId}/orders`
- **Query params**:
  - `status` (por exemplo, `EM_ROTA`, `ENTREGUE`).

### 6.4. Atribuir entregador a pedido

- **PATCH** `/api/v1/orders/{orderId}/assign-courier`

- **Request body**:

  ```json
  { "courierId": "uuid-entregador" }
  ```

---

## 7. Pagamentos (Payments)

### 7.1. Registrar pagamento de pedido

- **POST** `/api/v1/orders/{orderId}/payments`

- **Request body**:

  ```json
  {
    "method": "PIX",
    "status": "APROVADO",
    "amount": 88.30,
    "transactionId": "id-gateway"
  }
  ```

### 7.2. Webhook de pagamento (opcional para Fase 1)

- **POST** `/api/v1/payments/webhook/{provider}`

- **Descrição**: endpoint para gateways notificarem atualizações de pagamento.

---

## 8. Saúde da Aplicação

### 8.1. Health check

- **GET** `/actuator/health`
- **Descrição**: endpoint padrão do Spring Boot Actuator para health check.

---

## 9. Próximos Passos

- Detalhar contratos de autenticação/autorização (headers, escopos, papéis) para cada endpoint sensível.
- Especificar mais precisamente os códigos de erro (`code`, `message`) por cenário de falha.
- Refinar modelos de request/response conforme o domínio evoluir (ex.: inclusão de promoções, cupons, taxas extras).
