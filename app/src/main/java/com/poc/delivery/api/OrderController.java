package com.poc.delivery.api;

import java.util.UUID;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.common.logging.LogEvent;
import com.poc.delivery.domain.usecase.CreateOrderUseCase;
import com.poc.delivery.domain.usecase.GetOrderUseCase;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> createOrder(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = createOrderUseCase.execute(request);
        LOGGER.info("[{}] Pedido criado com sucesso, id={}", LogEvent.ORDER_CREATED.code(), response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> getOrder(@PathVariable UUID id) {
        PedidoResponse response = getOrderUseCase.execute(id);
        LOGGER.info("[{}] Pedido consultado com sucesso, id={}", LogEvent.ORDER_RETRIEVED.code(), id);
        return ResponseEntity.ok(response);
    }
}
