package com.poc.delivery.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemPedidoEntityTest {

    @Test
    void deveManterValoresNosGettersESetters() {
        UUID produtoId = UUID.randomUUID();
        String nomeProduto = "produto";
        Integer quantidade = 2;
        BigDecimal precoUnitario = BigDecimal.TEN;
        BigDecimal subtotal = BigDecimal.valueOf(20);
        String observacoes = "obs";

        ItemPedidoEntity entity = new ItemPedidoEntity();
        entity.setProdutoId(produtoId);
        entity.setNomeProduto(nomeProduto);
        entity.setQuantidade(quantidade);
        entity.setPrecoUnitario(precoUnitario);
        entity.setSubtotal(subtotal);
        entity.setObservacoes(observacoes);

        Assertions.assertThat(entity.getProdutoId()).isEqualTo(produtoId);
        Assertions.assertThat(entity.getNomeProduto()).isEqualTo(nomeProduto);
        Assertions.assertThat(entity.getQuantidade()).isEqualTo(quantidade);
        Assertions.assertThat(entity.getPrecoUnitario()).isEqualTo(precoUnitario);
        Assertions.assertThat(entity.getSubtotal()).isEqualTo(subtotal);
        Assertions.assertThat(entity.getObservacoes()).isEqualTo(observacoes);
    }

    @Test
    void deveConstruirComConstrutorCompleto() {
        UUID produtoId = UUID.randomUUID();
        String nomeProduto = "produto";
        Integer quantidade = 2;
        BigDecimal precoUnitario = BigDecimal.TEN;
        BigDecimal subtotal = BigDecimal.valueOf(20);
        String observacoes = "obs";

        ItemPedidoEntity entity = new ItemPedidoEntity(
            produtoId,
            nomeProduto,
            quantidade,
            precoUnitario,
            subtotal,
            observacoes
        );

        Assertions.assertThat(entity.getProdutoId()).isEqualTo(produtoId);
        Assertions.assertThat(entity.getNomeProduto()).isEqualTo(nomeProduto);
        Assertions.assertThat(entity.getQuantidade()).isEqualTo(quantidade);
        Assertions.assertThat(entity.getPrecoUnitario()).isEqualTo(precoUnitario);
        Assertions.assertThat(entity.getSubtotal()).isEqualTo(subtotal);
        Assertions.assertThat(entity.getObservacoes()).isEqualTo(observacoes);
    }
}
