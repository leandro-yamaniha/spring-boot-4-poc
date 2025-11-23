# HIST-001 - Diagrama de Sequência: Criação de Pedido

## Fluxo Principal - Criação de Pedido com Sucesso

```mermaid
sequenceDiagram
    actor Cliente
    participant API as OrderController
    participant UseCase as CreateOrderUseCase
    participant Validator as OrderValidator
    participant Calculator as PriceCalculator
    participant Repo as OrderRepository
    participant DB as PostgreSQL

    Cliente->>API: POST /api/v1/orders
    Note over Cliente,API: Request Body:<br/>{clienteId, lojaId,<br/>enderecoId, itens[]}
    
    API->>UseCase: execute(request)
    
    UseCase->>Validator: validateClient(clienteId)
    Validator-->>UseCase: Cliente válido
    
    UseCase->>Validator: validateLoja(lojaId)
    Validator-->>UseCase: Loja válida
    
    UseCase->>Validator: validateEndereco(enderecoId, clienteId)
    Validator-->>UseCase: Endereço válido
    
    UseCase->>Validator: validateItens(itens)
    Validator-->>UseCase: Itens válidos
    
    UseCase->>Calculator: calculateTotal(itens, lojaId)
    Note over Calculator: Soma itens<br/>+ taxa entrega<br/>- descontos
    Calculator-->>UseCase: Total calculado
    
    UseCase->>UseCase: createPedidoDomain()
    Note over UseCase: Status: CRIADO<br/>Timestamp: now()
    
    UseCase->>Repo: save(pedido)
    Repo->>DB: INSERT INTO pedidos
    Repo->>DB: INSERT INTO itens_pedido
    DB-->>Repo: Pedido salvo
    Repo-->>UseCase: Pedido persistido
    
    UseCase-->>API: PedidoResponse
    API-->>Cliente: 201 Created
    Note over Cliente,API: Response Body:<br/>{id, status, total,<br/>createdAt, itens[]}
```

## Fluxo Alternativo 1 - Cliente Inválido

```mermaid
sequenceDiagram
    actor Cliente
    participant API as OrderController
    participant UseCase as CreateOrderUseCase
    participant Validator as OrderValidator
    participant Handler as GlobalExceptionHandler

    Cliente->>API: POST /api/v1/orders
    API->>UseCase: execute(request)
    UseCase->>Validator: validateClient(clienteId)
    Validator-->>UseCase: ClienteNotFoundException
    UseCase-->>API: throw ClienteNotFoundException
    API->>Handler: handleClienteNotFound()
    Handler-->>API: ApiErrorResponse
    API-->>Cliente: 404 Not Found
    Note over Cliente,API: {code: "CLIENTE_NAO_ENCONTRADO",<br/>message: "Cliente não encontrado"}
```

## Fluxo Alternativo 2 - Validação de Itens Falha

```mermaid
sequenceDiagram
    actor Cliente
    participant API as OrderController
    participant UseCase as CreateOrderUseCase
    participant Validator as OrderValidator
    participant Handler as GlobalExceptionHandler

    Cliente->>API: POST /api/v1/orders
    API->>UseCase: execute(request)
    UseCase->>Validator: validateClient(clienteId)
    Validator-->>UseCase: OK
    UseCase->>Validator: validateLoja(lojaId)
    Validator-->>UseCase: OK
    UseCase->>Validator: validateItens(itens)
    Note over Validator: Produto não existe<br/>ou quantidade <= 0
    Validator-->>UseCase: ValidationException
    UseCase-->>API: throw ValidationException
    API->>Handler: handleValidation()
    Handler-->>API: ApiErrorResponse
    API-->>Cliente: 400 Bad Request
    Note over Cliente,API: {code: "VALIDACAO_FALHOU",<br/>message: "Produto inválido",<br/>details: [...]}
```

## Fluxo Alternativo 3 - Erro de Persistência

```mermaid
sequenceDiagram
    actor Cliente
    participant API as OrderController
    participant UseCase as CreateOrderUseCase
    participant Repo as OrderRepository
    participant DB as PostgreSQL
    participant Handler as GlobalExceptionHandler

    Cliente->>API: POST /api/v1/orders
    API->>UseCase: execute(request)
    Note over UseCase: Validações OK<br/>Cálculo OK<br/>Domínio criado
    UseCase->>Repo: save(pedido)
    Repo->>DB: INSERT INTO pedidos
    DB-->>Repo: Constraint Violation
    Repo-->>UseCase: DataIntegrityException
    UseCase-->>API: throw DataIntegrityException
    API->>Handler: handleDataIntegrity()
    Handler-->>API: ApiErrorResponse
    API-->>Cliente: 422 Unprocessable Entity
    Note over Cliente,API: {code: "ERRO_INTEGRIDADE",<br/>message: "Erro ao salvar pedido"}
```

## Componentes Envolvidos

### Controller Layer
- **OrderController**: Recebe requisição HTTP, delega para use case, retorna response

### Use Cases Layer
- **CreateOrderUseCase**: Orquestra validações, cálculos e persistência (Single Responsibility)
- **OrderValidator**: Valida cliente, loja, endereço e itens
- **PriceCalculator**: Calcula total do pedido

### Repository Layer
- **OrderRepository**: Interface JPA para persistência de pedidos

### Exception Handling
- **GlobalExceptionHandler**: Mapeia exceções para respostas HTTP padronizadas

## Códigos de Erro

| Código | HTTP Status | Descrição |
|--------|-------------|-----------|
| CLIENTE_NAO_ENCONTRADO | 404 | Cliente não existe |
| LOJA_NAO_ENCONTRADA | 404 | Loja não existe |
| ENDERECO_NAO_ENCONTRADO | 404 | Endereço não existe |
| PRODUTO_NAO_ENCONTRADO | 404 | Produto não existe |
| VALIDACAO_FALHOU | 400 | Dados de entrada inválidos |
| ERRO_INTEGRIDADE | 422 | Violação de integridade referencial |
| INTERNAL_ERROR | 500 | Erro interno do servidor |

## Tempos Esperados

- **Validações**: < 100ms
- **Cálculo de total**: < 50ms
- **Persistência**: < 200ms
- **Total (happy path)**: < 500ms
