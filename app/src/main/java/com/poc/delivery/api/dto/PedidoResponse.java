package com.poc.delivery.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.poc.delivery.domain.model.StatusPedido;

public record PedidoResponse(
    UUID id,
    UUID clienteId,
    UUID lojaId,
    UUID enderecoId,
    List<ItemPedidoResponse> itens,
    StatusPedido status,
    BigDecimal total,
    BigDecimal taxaEntrega,
    BigDecimal desconto,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
