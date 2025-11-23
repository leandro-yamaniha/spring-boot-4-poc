package com.poc.delivery.infrastructure.persistence.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.ItemPedidoEntity;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoMapper {
    
    PedidoEntity toEntity(Pedido pedido);
    
    Pedido toDomain(PedidoEntity entity);
    
    ItemPedidoEntity toItemEntity(ItemDePedido item);
    
    ItemDePedido toItemDomain(ItemPedidoEntity entity);
    
    List<ItemPedidoEntity> toItemEntityList(List<ItemDePedido> itens);
    
    List<ItemDePedido> toItemDomainList(List<ItemPedidoEntity> itens);
    
    @AfterMapping
    default void setIdAfterMapping(PedidoEntity entity, @MappingTarget Pedido.Builder builder) {
        builder.id(entity.getId());
    }
}
