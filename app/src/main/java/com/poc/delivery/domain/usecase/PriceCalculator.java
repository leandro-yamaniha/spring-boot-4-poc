package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.poc.delivery.domain.model.ItemDePedido;

@Component
public class PriceCalculator {
    
    public BigDecimal calculateTaxaEntrega(List<ItemDePedido> itens) {
        if (itens == null || itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotalItens = itens.stream()
            .map(ItemDePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal limiteFreteGratis = BigDecimal.valueOf(100);
        if (subtotalItens.compareTo(limiteFreteGratis) >= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(9.90);
    }
    
    public BigDecimal calculateDesconto(List<ItemDePedido> itens) {
        if (itens == null || itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int quantidadeTotal = itens.stream()
            .mapToInt(ItemDePedido::getQuantidade)
            .sum();

        if (quantidadeTotal < 3) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotalItens = itens.stream()
            .map(ItemDePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentualDesconto = BigDecimal.valueOf(0.05);
        return subtotalItens.multiply(percentualDesconto);
    }
}
