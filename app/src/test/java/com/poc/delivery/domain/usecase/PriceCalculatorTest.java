package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.poc.delivery.domain.model.ItemDePedido;

class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    void deveRetornarTaxaZeroQuandoSemItens() {
        BigDecimal taxa = calculator.calculateTaxaEntrega(null);
        Assertions.assertThat(taxa).isEqualTo(BigDecimal.ZERO);

        taxa = calculator.calculateTaxaEntrega(List.of());
        Assertions.assertThat(taxa).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void deveAplicarFreteQuandoSubtotalMenorQueLimite() {
        ItemDePedido item = itemDePedido(BigDecimal.valueOf(30), 2);
        BigDecimal taxa = calculator.calculateTaxaEntrega(List.of(item));
        Assertions.assertThat(taxa).isEqualTo(BigDecimal.valueOf(9.90));
    }

    @Test
    void deveDarFreteGratisQuandoSubtotalMaiorOuIgualLimite() {
        ItemDePedido item = itemDePedido(BigDecimal.valueOf(50), 2);
        BigDecimal taxa = calculator.calculateTaxaEntrega(List.of(item));
        Assertions.assertThat(taxa).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void deveRetornarDescontoZeroQuandoSemItens() {
        BigDecimal desconto = calculator.calculateDesconto(null);
        Assertions.assertThat(desconto).isEqualTo(BigDecimal.ZERO);

        desconto = calculator.calculateDesconto(List.of());
        Assertions.assertThat(desconto).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void deveRetornarDescontoZeroQuandoQuantidadeTotalMenorQueTres() {
        ItemDePedido item = itemDePedido(BigDecimal.TEN, 2);
        BigDecimal desconto = calculator.calculateDesconto(List.of(item));
        Assertions.assertThat(desconto).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void deveAplicarDescontoQuandoQuantidadeTotalMaiorOuIgualTres() {
        ItemDePedido item = itemDePedido(BigDecimal.TEN, 3);
        BigDecimal desconto = calculator.calculateDesconto(List.of(item));

        BigDecimal subtotal = item.getSubtotal();
        BigDecimal esperado = subtotal.multiply(BigDecimal.valueOf(0.05));

        Assertions.assertThat(desconto).isEqualTo(esperado);
    }

    private ItemDePedido itemDePedido(BigDecimal precoUnitario, int quantidade) {
        return ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto("qualquer")
            .quantidade(quantidade)
            .precoUnitario(precoUnitario)
            .observacoes(null)
            .build();
    }
}
