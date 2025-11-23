package com.poc.delivery.infrastructure.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.ItemPedidoEntity;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;

import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {
    
    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setClienteId(pedido.getClienteId());
        entity.setLojaId(pedido.getLojaId());
        entity.setEnderecoId(pedido.getEnderecoId());
        entity.setStatus(pedido.getStatus());
        entity.setTotal(pedido.getTotal());
        entity.setTaxaEntrega(pedido.getTaxaEntrega());
        entity.setDesconto(pedido.getDesconto());
        entity.setCreatedAt(pedido.getCreatedAt());
        entity.setUpdatedAt(pedido.getUpdatedAt());
        
        List<ItemPedidoEntity> itensEntity = pedido.getItens().stream()
            .map(this::toItemEntity)
            .collect(Collectors.toList());
        entity.setItens(itensEntity);
        
        return entity;
    }
    
    public Pedido toDomain(PedidoEntity entity) {
        List<ItemDePedido> itens = entity.getItens().stream()
            .map(this::toItemDomain)
            .collect(Collectors.toList());
        
        Pedido pedido = Pedido.builder()
            .clienteId(entity.getClienteId())
            .lojaId(entity.getLojaId())
            .enderecoId(entity.getEnderecoId())
            .itens(itens)
            .taxaEntrega(entity.getTaxaEntrega())
            .desconto(entity.getDesconto())
            .build();
        
        pedido.setId(entity.getId());
        
        return pedido;
    }
    
    private ItemPedidoEntity toItemEntity(ItemDePedido item) {
        return new ItemPedidoEntity(
            item.getProdutoId(),
            item.getNomeProduto(),
            item.getQuantidade(),
            item.getPrecoUnitario(),
            item.getSubtotal(),
            item.getObservacoes()
        );
    }
    
    private ItemDePedido toItemDomain(ItemPedidoEntity entity) {
        return ItemDePedido.builder()
            .produtoId(entity.getProdutoId())
            .nomeProduto(entity.getNomeProduto())
            .quantidade(entity.getQuantidade())
            .precoUnitario(entity.getPrecoUnitario())
            .observacoes(entity.getObservacoes())
            .build();
    }
}
