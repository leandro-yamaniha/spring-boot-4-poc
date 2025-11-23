package com.poc.delivery.domain.usecase;

import java.util.List;

import org.springframework.stereotype.Component;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;

@Component
public class OrderValidator {
    
    public void validate(PedidoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("pedidoRequestNaoPodeSerNulo");
        }
        validateIds(request);
        validateItens(request.itens());
    }
    
    private void validateIds(PedidoRequest request) {
        if (request.clienteId() == null) {
            throw new IllegalArgumentException("clienteIdObrigatorio");
        }
        if (request.lojaId() == null) {
            throw new IllegalArgumentException("lojaIdObrigatorio");
        }
        if (request.enderecoId() == null) {
            throw new IllegalArgumentException("enderecoIdObrigatorio");
        }
    }
    
    private void validateItens(List<ItemPedidoRequest> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("pedidoDeveTerAoMenosUmItem");
        }
        for (ItemPedidoRequest item : itens) {
            if (item.produtoId() == null) {
                throw new IllegalArgumentException("produtoIdObrigatorio");
            }
            if (item.quantidade() == null || item.quantidade() <= 0) {
                throw new IllegalArgumentException("quantidadeDeveSerPositiva");
            }
            if (item.precoUnitario() == null || item.precoUnitario().signum() <= 0) {
                throw new IllegalArgumentException("precoUnitarioDeveSerPositivo");
            }
        }
    }
}
