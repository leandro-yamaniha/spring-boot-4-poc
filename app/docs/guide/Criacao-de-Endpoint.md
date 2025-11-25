# Template para Criação de Endpoints - Spring Boot 4 + Clean Architecture

## 🎯 Visão Geral

Este documento fornece um template completo para criar novos endpoints seguindo as melhores práticas do projeto:

- **Arquitetura**: Clean Architecture com Use Cases
- **Princípios**: SOLID, TDD, Clean Code
- **Tecnologias**: Spring Boot 4, Java 25, JPA, MapStruct, PostgreSQL

## 📋 Estrutura de Camadas

```
com.poc.delivery
├── api/                    # Camada de API
│   ├── dto/               # Data Transfer Objects
│   ├── OrderController.java
│   └── GlobalExceptionHandler.java
├── domain/                # Camada de Domínio
│   ├── model/            # Modelos de domínio
│   ├── usecase/          # Casos de uso
│   └── exception/        # Exceções de domínio
└── infrastructure/       # Camada de Infraestrutura
    ├── persistence/      # JPA Entities, Repositories
    └── mapper/           # MapStruct mappers
```

## 🚀 Passo a Passo para Criar um Endpoint

### 1. Definir a Funcionalidade

**Exemplo**: Criar endpoint para buscar pedido por ID

- **Método HTTP**: GET
- **URL**: `/api/v1/orders/{id}`
- **Resposta**: PedidoResponse (200 OK) ou 404 Not Found

### 2. Criar DTOs (Data Transfer Objects)

#### Request DTO (se aplicável)
```java
package com.poc.delivery.api.dto;

public record GetOrderRequest(
    @NotNull(message = "id é obrigatório")
    UUID id
) {
}
```

#### Response DTO
```java
package com.poc.delivery.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID clienteId,
    UUID lojaId,
    UUID enderecoId,
    List<OrderItemResponse> itens,
    StatusPedido status,
    BigDecimal total,
    BigDecimal taxaEntrega,
    BigDecimal desconto,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
```

### 3. Criar Modelo de Domínio (se necessário)

```java
package com.poc.delivery.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Pedido {
    private UUID id;
    private UUID clienteId;
    private UUID lojaId;
    private UUID enderecoId;
    private List<ItemDePedido> itens;
    private StatusPedido status;
    private BigDecimal total;
    private BigDecimal taxaEntrega;
    private BigDecimal desconto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor, getters, setters, métodos de negócio
}
```

### 4. Criar Use Case

```java
package com.poc.delivery.domain.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.poc.delivery.api.dto.OrderResponse;
import com.poc.delivery.common.logging.LogEvent;
import com.poc.delivery.domain.exception.OrderNotFoundException;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

@Component
public class GetOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrderUseCase.class);

    private final PedidoRepository repository;

    public GetOrderUseCase(PedidoRepository repository) {
        this.repository = repository;
    }

    public OrderResponse execute(UUID id) {
        // Buscar pedido no repositório
        PedidoEntity entity = repository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + id));

        // Converter para domínio
        Pedido pedido = mapper.toDomain(entity);

        LOGGER.info("[{}] Pedido encontrado, id={}", LogEvent.ORDER_FOUND.code(), pedido.getId());

        // Converter para response
        return toResponse(pedido);
    }

    private OrderResponse toResponse(Pedido pedido) {
        List<OrderItemResponse> itensResponse = pedido.getItens().stream()
            .map(item -> new OrderItemResponse(
                item.getProdutoId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal(),
                item.getObservacoes()
            ))
            .toList();

        return new OrderResponse(
            pedido.getId(),
            pedido.getClienteId(),
            pedido.getLojaId(),
            pedido.getEnderecoId(),
            itensResponse,
            pedido.getStatus(),
            pedido.getTotal(),
            pedido.getTaxaEntrega(),
            pedido.getDesconto(),
            pedido.getCreatedAt(),
            pedido.getUpdatedAt()
        );
    }
}
```

### 5. Criar Controller

```java
package com.poc.delivery.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.delivery.api.dto.OrderResponse;
import com.poc.delivery.common.logging.LogEvent;
import com.poc.delivery.domain.usecase.GetOrderUseCase;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final GetOrderUseCase getOrderUseCase;

    public OrderController(GetOrderUseCase getOrderUseCase) {
        this.getOrderUseCase = getOrderUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        OrderResponse response = getOrderUseCase.execute(id);
        LOGGER.info("[{}] Pedido retornado com sucesso, id={}", LogEvent.ORDER_FOUND.code(), response.id());
        return ResponseEntity.ok(response);
    }
}
```

### 6. Criar Repository Interface (Domínio)

```java
package com.poc.delivery.domain.repository;

import com.poc.delivery.domain.model.Pedido;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository {
    Optional<Pedido> findById(UUID id);
    Pedido save(Pedido pedido);
    // Outros métodos...
}
```

### 7. Implementar Repository (Infraestrutura)

```java
package com.poc.delivery.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poc.delivery.domain.repository.PedidoRepository;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PedidoRepositoryImpl implements PedidoRepository {

    private final JpaPedidoRepository jpaRepository;
    private final PedidoMapper mapper;

    public PedidoRepositoryImpl(JpaPedidoRepository jpaRepository, PedidoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Pedido> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = mapper.toEntity(pedido);
        PedidoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}

// Interface JPA
interface JpaPedidoRepository extends JpaRepository<PedidoEntity, UUID> {
}
```

### 8. Criar MapStruct Mapper

```java
package com.poc.delivery.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "itens", ignore = true) // Mapeamento customizado se necessário
    Pedido toDomain(PedidoEntity entity);

    @Mapping(target = "itens", ignore = true)
    PedidoEntity toEntity(Pedido domain);
}
```

### 9. Criar Exceções de Domínio

```java
package com.poc.delivery.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
```

### 10. Configurar Tratamento de Erros

```java
package com.poc.delivery.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.poc.delivery.domain.exception.OrderNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Pedido não encontrado", ex.getMessage()));
    }
}
```

## 🧪 TDD - Testes Primeiro

### 1. Teste de Use Case
```java
@SpringBootTest
class GetOrderUseCaseTest {

    @Autowired
    private GetOrderUseCase useCase;

    @Test
    void deveBuscarPedidoExistente() {
        // Given
        UUID pedidoId = UUID.randomUUID();

        // When
        OrderResponse response = useCase.execute(pedidoId);

        // Then
        assertThat(response.id()).isEqualTo(pedidoId);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExiste() {
        // Given
        UUID pedidoId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> useCase.execute(pedidoId))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessage("Pedido não encontrado: " + pedidoId);
    }
}
```

### 2. Teste de Controller (Integração)
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveRetornarPedidoQuandoExistir() {
        // Given
        UUID pedidoId = UUID.randomUUID();

        // When
        ResponseEntity<OrderResponse> response = restTemplate
            .getForEntity("/api/v1/orders/" + pedidoId, OrderResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(pedidoId);
    }
}
```

## ✅ Checklist de Qualidade

- [ ] **TDD**: Testes criados antes da implementação
- [ ] **SOLID**: Single Responsibility em cada classe
- [ ] **Clean Code**: Nomes descritivos, sem comentários desnecessários
- [ ] **Validação**: DTOs com anotações @Valid e mensagens claras
- [ ] **Logging**: Eventos importantes logados com LogEvent
- [ ] **Tratamento de Erros**: Exceções específicas do domínio
- [ ] **Cobertura**: 100% nos novos códigos
- [ ] **Checkstyle**: Zero violações
- [ ] **SonarQube**: Quality Gate aprovado

## 📚 Referências

- [Clean Architecture](planejamento/analise-arquiteturas-backend.md)
- [Guia de Logging](app/docs/guide/LOGGING.md)
- [Guia do Gradle](app/docs/guide/GRADLE.md)
- [Exemplo Implementado](src/main/java/com/poc/delivery/api/OrderController.java)

## 🎯 Princípios Gerais

1. **Um Use Case por ação**: Cada endpoint mapeia para um use case específico
2. **Domínio no centro**: Regras de negócio ficam isoladas da infraestrutura
3. **DTOs imutáveis**: Usar `record` para requests/responses
4. **Injeção de dependências**: Via construtor, nunca campos públicos
5. **Exceções específicas**: Nunca expor detalhes técnicos
6. **Logs estruturados**: Usar LogEvent para eventos importantes
7. **Testes abrangentes**: Unitários + Integração + API
