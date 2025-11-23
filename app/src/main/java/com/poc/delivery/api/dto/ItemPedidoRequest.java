package com.poc.delivery.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemPedidoRequest(
    @NotNull(message = "produtoId é obrigatório")
    UUID produtoId,
    
    @NotNull(message = "quantidade é obrigatória")
    @Min(value = 1, message = "quantidade deve ser maior que zero")
    Integer quantidade,
    
    @NotNull(message = "precoUnitario é obrigatório")
    @Min(value = 0, message = "precoUnitario deve ser maior ou igual a zero")
    BigDecimal precoUnitario,
    
    @Size(max = 500, message = "observacoes deve ter no máximo 500 caracteres")
    String observacoes
) {
}
