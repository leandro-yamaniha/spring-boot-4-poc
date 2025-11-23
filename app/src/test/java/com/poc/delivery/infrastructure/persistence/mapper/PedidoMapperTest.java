package com.poc.delivery.infrastructure.persistence.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.domain.model.StatusPedido;
import com.poc.delivery.infrastructure.persistence.entity.ItemPedidoEntity;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;

class PedidoMapperTest {

    private final PedidoMapper mapper = Mappers.getMapper(PedidoMapper.class);

    @Test
    void deveMapearPedidoDomainParaEntity() {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        ItemDePedido item = ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto("produto")
            .quantidade(2)
            .precoUnitario(BigDecimal.TEN)
            .observacoes("obs")
            .build();

        Pedido pedido = Pedido.builder()
            .clienteId(clienteId)
            .lojaId(lojaId)
            .enderecoId(enderecoId)
            .itens(List.of(item))
            .taxaEntrega(BigDecimal.ONE)
            .desconto(BigDecimal.ZERO)
            .build();

        PedidoEntity entity = mapper.toEntity(pedido);

        Assertions.assertThat(entity.getClienteId()).isEqualTo(clienteId);
        Assertions.assertThat(entity.getLojaId()).isEqualTo(lojaId);
        Assertions.assertThat(entity.getEnderecoId()).isEqualTo(enderecoId);
        Assertions.assertThat(entity.getTaxaEntrega()).isEqualTo(pedido.getTaxaEntrega());
        Assertions.assertThat(entity.getDesconto()).isEqualTo(pedido.getDesconto());
        Assertions.assertThat(entity.getItens()).hasSize(1);

        ItemPedidoEntity mappedItem = entity.getItens().getFirst();
        Assertions.assertThat(mappedItem.getProdutoId()).isEqualTo(item.getProdutoId());
        Assertions.assertThat(mappedItem.getNomeProduto()).isEqualTo(item.getNomeProduto());
        Assertions.assertThat(mappedItem.getQuantidade()).isEqualTo(item.getQuantidade());
        Assertions.assertThat(mappedItem.getPrecoUnitario()).isEqualTo(item.getPrecoUnitario());
    }

    @Test
    void deveMapearPedidoEntityParaDomainEAplicarId() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        ItemPedidoEntity itemEntity = new ItemPedidoEntity(
            UUID.randomUUID(),
            "produto",
            1,
            BigDecimal.TEN,
            BigDecimal.TEN,
            "obs"
        );

        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setClienteId(clienteId);
        entity.setLojaId(lojaId);
        entity.setEnderecoId(enderecoId);
        entity.setItens(List.of(itemEntity));
        entity.setStatus(StatusPedido.CRIADO);
        entity.setTotal(BigDecimal.TEN);
        entity.setTaxaEntrega(BigDecimal.ONE);
        entity.setDesconto(BigDecimal.ZERO);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());

        Pedido pedido = mapper.toDomain(entity);

        Assertions.assertThat(pedido.getId()).isEqualTo(id);
        Assertions.assertThat(pedido.getClienteId()).isEqualTo(clienteId);
        Assertions.assertThat(pedido.getLojaId()).isEqualTo(lojaId);
        Assertions.assertThat(pedido.getEnderecoId()).isEqualTo(enderecoId);
        Assertions.assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CRIADO);
        Assertions.assertThat(pedido.getItens()).hasSize(1);

        ItemDePedido mappedItem = pedido.getItens().getFirst();
        Assertions.assertThat(mappedItem.getProdutoId()).isEqualTo(itemEntity.getProdutoId());
        Assertions.assertThat(mappedItem.getNomeProduto()).isEqualTo(itemEntity.getNomeProduto());
        Assertions.assertThat(mappedItem.getQuantidade()).isEqualTo(itemEntity.getQuantidade());
        Assertions.assertThat(mappedItem.getPrecoUnitario()).isEqualTo(itemEntity.getPrecoUnitario());
    }
}
