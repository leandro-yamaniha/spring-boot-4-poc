package com.poc.delivery.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PedidoTest {

    private static final String PRODUTO_NOME = "produto";

    @Test
    void deveCalcularTotalComTaxaEDesconto() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        BigDecimal taxa = BigDecimal.valueOf(5);
        BigDecimal desconto = BigDecimal.valueOf(3);

        Pedido pedido = Pedido.builder()
            .clienteId(UUID.randomUUID())
            .lojaId(UUID.randomUUID())
            .enderecoId(UUID.randomUUID())
            .itens(List.of(item))
            .taxaEntrega(taxa)
            .desconto(desconto)
            .build();

        BigDecimal esperado = item.getSubtotal().add(taxa).subtract(desconto);
        Assertions.assertThat(pedido.getTotal()).isEqualTo(esperado);
    }

    @Test
    void deveLancarExcecaoQuandoSemItens() {
        Assertions.assertThatThrownBy(() -> Pedido.builder()
                .clienteId(UUID.randomUUID())
                .lojaId(UUID.randomUUID())
                .enderecoId(UUID.randomUUID())
                .itens(List.of())
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRealizarTransicoesDeStatusValidas() {
        Pedido pedido = pedidoBasico();

        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CRIADO);

        pedido.confirmar();
        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CONFIRMADO);

        pedido.marcarComoPronto();
        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.PRONTO);

        pedido.iniciarEntrega();
        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EM_ENTREGA);

        pedido.finalizarEntrega();
        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.ENTREGUE);
    }

    @Test
    void deveLancarExcecaoAoConfirmarPedidoNaoCriado() {
        Pedido pedido = pedidoBasico();
        pedido.confirmar();

        Assertions.assertThatThrownBy(pedido::confirmar)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoCancelarPedidoEntregue() {
        Pedido pedido = pedidoBasico();
        pedido.confirmar();
        pedido.marcarComoPronto();
        pedido.iniciarEntrega();
        pedido.finalizarEntrega();

        Assertions.assertThatThrownBy(pedido::cancelar)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoAoCancelarPedidoJaCancelado() {
        Pedido pedido = pedidoBasico();
        pedido.cancelar();

        Assertions.assertThatThrownBy(pedido::cancelar)
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoQuandoTaxaEntregaNegativa() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        Assertions.assertThatThrownBy(() -> Pedido.builder()
                .clienteId(UUID.randomUUID())
                .lojaId(UUID.randomUUID())
                .enderecoId(UUID.randomUUID())
                .itens(List.of(item))
                .taxaEntrega(BigDecimal.valueOf(-1))
                .desconto(BigDecimal.ZERO)
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveLancarExcecaoQuandoDescontoNegativo() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        Assertions.assertThatThrownBy(() -> Pedido.builder()
                .clienteId(UUID.randomUUID())
                .lojaId(UUID.randomUUID())
                .enderecoId(UUID.randomUUID())
                .itens(List.of(item))
                .taxaEntrega(BigDecimal.ZERO)
                .desconto(BigDecimal.valueOf(-1))
                .build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRetornarListaDeItensImutavel() {
        Pedido pedido = pedidoBasico();

        Assertions.assertThatThrownBy(() -> pedido.getItens().add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deveConstruirPedidoComBuilderAdicionarItem() {
        ItemDePedido item1 = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto("produto1")
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        ItemDePedido item2 = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto("produto2")
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        Pedido pedido = Pedido.builder()
            .clienteId(UUID.randomUUID())
            .lojaId(UUID.randomUUID())
            .enderecoId(UUID.randomUUID())
            .adicionarItem(item1)
            .adicionarItem(item2)
            .taxaEntrega(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .build();

        Assertions.assertThat(pedido.getItens())
            .hasSize(2)
            .containsExactly(item1, item2);
    }

    @Test
    void deveImplementarEqualsEHashCodeBaseadosNoId() {
        Pedido pedido1 = pedidoBasico();
        Pedido pedido2 = pedidoBasico();
        Pedido pedido3 = pedidoBasico();

        UUID id = UUID.randomUUID();
        pedido1.setId(id);
        pedido2.setId(id);
        pedido3.setId(UUID.randomUUID());

        Assertions.assertThat(pedido1)
            .isEqualTo(pedido2)
            .hasSameHashCodeAs(pedido2)
            .isNotEqualTo(pedido3);
    }

    private Pedido pedidoBasico() {
        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto(PRODUTO_NOME)
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes(null)
            .build();

        return Pedido.builder()
            .clienteId(UUID.randomUUID())
            .lojaId(UUID.randomUUID())
            .enderecoId(UUID.randomUUID())
            .itens(List.of(item))
            .taxaEntrega(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .build();
    }
}
