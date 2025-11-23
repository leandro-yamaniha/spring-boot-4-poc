package com.poc.delivery.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemDePedidoTest {

    private static final String PRODUTO_NOME = "produto";
    private static final String OBS = "obs";

    @Test
    void deveCalcularSubtotalCorretamente() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(3)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        Assertions.assertThat(item.getSubtotal()).isEqualTo(BigDecimal.valueOf(30));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeMenorOuIgualZero() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(PRODUTO_NOME)
                .quantidade(0)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarExcecaoQuandoPrecoUnitarioMenorOuIgualZero() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(PRODUTO_NOME)
                .quantidade(1)
                .precoUnitario(BigDecimal.ZERO)
                .observacoes(null)
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNomeProdutoVazio() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(" ")
                .quantidade(1)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoIdNulo() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(null)
                .nomeProduto(PRODUTO_NOME)
                .quantidade(1)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNomeProdutoNulo() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(null)
                .quantidade(1)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeNula() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(PRODUTO_NOME)
                .quantidade(null)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveLancarExcecaoQuandoPrecoUnitarioNulo() {
        Assertions.assertThatThrownBy(() -> ItemDePedido.builder()
                .produtoId(UUID.randomUUID())
                .nomeProduto(PRODUTO_NOME)
                .quantidade(1)
                .precoUnitario(null)
                .observacoes(null)
                .build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deveCompararItensPorValorEqualsEHashCode() {
        UUID produtoId = UUID.randomUUID();

        ItemDePedido item1 = ItemDePedido.builder()
            .produtoId(produtoId)
            .nomeProduto(PRODUTO_NOME)
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(OBS)
            .build();

        ItemDePedido item2 = ItemDePedido.builder()
            .produtoId(produtoId)
            .nomeProduto(PRODUTO_NOME)
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(OBS)
            .build();

        ItemDePedido item3 = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(1)
            .precoUnitario(BigDecimal.ONE)
            .observacoes(null)
            .build();

        Assertions.assertThat(item1)
            .isEqualTo(item2)
            .hasSameHashCodeAs(item2)
            .isNotEqualTo(item3);
    }

    @Test
    void deveGerarToStringComCamposPrincipais() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(OBS)
            .build();

        Assertions.assertThat(item.toString())
            .contains(PRODUTO_NOME)
            .contains("quantidade")
            .contains("precoUnitario");
    }
}
