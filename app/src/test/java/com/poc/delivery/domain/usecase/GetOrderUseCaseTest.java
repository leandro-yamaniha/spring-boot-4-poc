package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.domain.model.StatusPedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

class GetOrderUseCaseTest {

    private PedidoRepository repository;
    private PedidoMapper mapper;
    private GetOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PedidoRepository.class);
        mapper = Mockito.mock(PedidoMapper.class);
        useCase = new GetOrderUseCase(repository, mapper);
    }

    @Test
    void deveRetornarPedidoQuandoEncontrado() {
        UUID pedidoId = UUID.randomUUID();
        PedidoEntity pedidoEntity = criarPedidoEntity(pedidoId);
        Pedido pedidoDominio = criarPedidoDominio(pedidoId);

        Mockito.when(repository.findById(pedidoId)).thenReturn(Optional.of(pedidoEntity));
        Mockito.when(mapper.toDomain(pedidoEntity)).thenReturn(pedidoDominio);

        PedidoResponse resultado = useCase.execute(pedidoId);

        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.id()).isEqualTo(pedidoId);
        Assertions.assertThat(resultado.clienteId()).isEqualTo(pedidoDominio.getClienteId());
        Assertions.assertThat(resultado.lojaId()).isEqualTo(pedidoDominio.getLojaId());
        Assertions.assertThat(resultado.enderecoId()).isEqualTo(pedidoDominio.getEnderecoId());
        Assertions.assertThat(resultado.status()).isEqualTo(StatusPedido.CRIADO);
        Assertions.assertThat(resultado.total()).isEqualTo(BigDecimal.TEN);

        Mockito.verify(repository).findById(pedidoId);
        Mockito.verify(mapper).toDomain(pedidoEntity);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        UUID pedidoId = UUID.randomUUID();

        Mockito.when(repository.findById(pedidoId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> useCase.execute(pedidoId))
            .isInstanceOf(OrderNotFoundException.class)
            .hasMessage("Pedido não encontrado: " + pedidoId);

        Mockito.verify(repository).findById(pedidoId);
        Mockito.verifyNoMoreInteractions(mapper);
    }

    private PedidoEntity criarPedidoEntity(UUID pedidoId) {
        PedidoEntity entity = Mockito.mock(PedidoEntity.class);
        Mockito.when(entity.getId()).thenReturn(pedidoId);
        return entity;
    }

    private Pedido criarPedidoDominio(UUID pedidoId) {
        return Pedido.builder()
            .id(pedidoId)
            .clienteId(UUID.randomUUID())
            .lojaId(UUID.randomUUID())
            .enderecoId(UUID.randomUUID())
            .adicionarItem(criarItemPedido())
            .taxaEntrega(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .build();
    }

    private ItemDePedido criarItemPedido() {
        return ItemDePedido.builder()
            .produtoId(UUID.randomUUID())
            .nomeProduto("Produto Teste")
            .quantidade(1)
            .precoUnitario(BigDecimal.TEN)
            .observacoes("Observação teste")
            .build();
    }
}
