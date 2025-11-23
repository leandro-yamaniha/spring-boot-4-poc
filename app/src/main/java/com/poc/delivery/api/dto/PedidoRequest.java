package com.poc.delivery.api.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
    @NotNull(message = "clienteId é obrigatório")
    UUID clienteId,
    
    @NotNull(message = "lojaId é obrigatório")
    UUID lojaId,
    
    @NotNull(message = "enderecoId é obrigatório")
    UUID enderecoId,
    
    @NotEmpty(message = "itens não pode ser vazio")
    @Valid
    List<ItemPedidoRequest> itens
) {
}
