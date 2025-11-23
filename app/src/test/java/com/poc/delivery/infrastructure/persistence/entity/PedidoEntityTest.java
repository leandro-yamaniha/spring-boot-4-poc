package com.poc.delivery.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.poc.delivery.domain.model.StatusPedido;

class PedidoEntityTest {

    @Test
    void deveManterValoresNosGettersESetters() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        ItemPedidoEntity item = new ItemPedidoEntity(
            UUID.randomUUID(),
            "produto",
            1,
            BigDecimal.TEN,
            BigDecimal.TEN,
            "obs"
        );
        List<ItemPedidoEntity> itens = List.of(item);
        StatusPedido status = StatusPedido.CRIADO;
        BigDecimal total = BigDecimal.valueOf(10);
        BigDecimal taxaEntrega = BigDecimal.ONE;
        BigDecimal desconto = BigDecimal.ZERO;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = createdAt.plusMinutes(1);

        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setClienteId(clienteId);
        entity.setLojaId(lojaId);
        entity.setEnderecoId(enderecoId);
        entity.setItens(itens);
        entity.setStatus(status);
        entity.setTotal(total);
        entity.setTaxaEntrega(taxaEntrega);
        entity.setDesconto(desconto);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        Assertions.assertThat(entity.getId()).isEqualTo(id);
        Assertions.assertThat(entity.getClienteId()).isEqualTo(clienteId);
        Assertions.assertThat(entity.getLojaId()).isEqualTo(lojaId);
        Assertions.assertThat(entity.getEnderecoId()).isEqualTo(enderecoId);
        Assertions.assertThat(entity.getItens()).containsExactly(item);
        Assertions.assertThat(entity.getStatus()).isEqualTo(status);
        Assertions.assertThat(entity.getTotal()).isEqualTo(total);
        Assertions.assertThat(entity.getTaxaEntrega()).isEqualTo(taxaEntrega);
        Assertions.assertThat(entity.getDesconto()).isEqualTo(desconto);
        Assertions.assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        Assertions.assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
