package com.poc.delivery.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoResponse(
    UUID produtoId,
    String nomeProduto,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal,
    String observacoes
) {
}
