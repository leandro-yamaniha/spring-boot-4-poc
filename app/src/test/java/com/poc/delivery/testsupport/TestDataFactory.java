package com.poc.delivery.testsupport;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static PedidoRequest pedidoRequestValido() {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        return pedidoRequest(clienteId, lojaId, enderecoId, produtoId);
    }

    public static PedidoRequest pedidoRequest(
        UUID clienteId,
        UUID lojaId,
        UUID enderecoId,
        UUID produtoId
    ) {
        ItemPedidoRequest item = new ItemPedidoRequest(
            produtoId,
            2,
            BigDecimal.TEN,
            null
        );
        return new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(item)
        );
    }

    public static Pedido pedidoDominioValido(
        UUID clienteId,
        UUID lojaId,
        UUID enderecoId,
        UUID produtoId
    ) {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(produtoId)
            .nomeProduto("produto")
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        return Pedido.builder()
            .clienteId(clienteId)
            .lojaId(lojaId)
            .enderecoId(enderecoId)
            .itens(List.of(item))
            .taxaEntrega(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .build();
    }
}
