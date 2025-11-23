package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.domain.model.StatusPedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

class CreateOrderUseCaseTest {

    private PedidoRepository repository;
    private PedidoMapper mapper;
    private OrderValidator validator;
    private PriceCalculator priceCalculator;
    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PedidoRepository.class);
        mapper = Mockito.mock(PedidoMapper.class);
        validator = Mockito.mock(OrderValidator.class);
        priceCalculator = Mockito.mock(PriceCalculator.class);
        useCase = new CreateOrderUseCase(repository, mapper, validator, priceCalculator);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedidoSalvoDominio = criarPedidoDominio(clienteId, lojaId, enderecoId, produtoId);
        UUID pedidoId = UUID.randomUUID();
        pedidoSalvoDominio.setId(pedidoId);

        PedidoEntity entity = criarPedidoEntity(clienteId, lojaId, enderecoId, pedidoId);

        BigDecimal taxaEntrega = entity.getTaxaEntrega();
        BigDecimal desconto = entity.getDesconto();

        Mockito.when(priceCalculator.calculateTaxaEntrega(Mockito.anyList())).thenReturn(taxaEntrega);
        Mockito.when(priceCalculator.calculateDesconto(Mockito.anyList())).thenReturn(desconto);
        Mockito.when(mapper.toEntity(Mockito.any(Pedido.class))).thenReturn(entity);
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDomain(entity)).thenReturn(pedidoSalvoDominio);

        PedidoRequest request = criarPedidoRequest(clienteId, lojaId, enderecoId, produtoId);
        PedidoResponse response = useCase.execute(request);

        Mockito.verify(validator).validate(request);
        Mockito.verify(repository).save(entity);

        Assertions.assertThat(response.id()).isEqualTo(pedidoId);
        Assertions.assertThat(response.clienteId()).isEqualTo(clienteId);
        Assertions.assertThat(response.lojaId()).isEqualTo(lojaId);
        Assertions.assertThat(response.enderecoId()).isEqualTo(enderecoId);
        Assertions.assertThat(response.total()).isEqualTo(pedidoSalvoDominio.getTotal());
        Assertions.assertThat(response.status()).isEqualTo(pedidoSalvoDominio.getStatus());
    }

    private PedidoRequest criarPedidoRequest(UUID clienteId, UUID lojaId, UUID enderecoId, UUID produtoId) {
        ItemPedidoRequest itemRequest = new ItemPedidoRequest(
            produtoId,
            2,
            BigDecimal.TEN,
            null
        );
        return new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(itemRequest)
        );
    }

    private Pedido criarPedidoDominio(UUID clienteId, UUID lojaId, UUID enderecoId, UUID produtoId) {
        List<ItemDePedido> itensDominio = List.of(
            ItemDePedido.builder()
                .produtoId(produtoId)
                .nomeProduto("produto")
                .quantidade(2)
                .precoUnitario(BigDecimal.TEN)
                .observacoes(null)
                .build()
        );

        BigDecimal taxaEntrega = BigDecimal.valueOf(9.90);
        BigDecimal desconto = BigDecimal.valueOf(5);

        return Pedido.builder()
            .clienteId(clienteId)
            .lojaId(lojaId)
            .enderecoId(enderecoId)
            .itens(itensDominio)
            .taxaEntrega(taxaEntrega)
            .desconto(desconto)
            .build();
    }

    private PedidoEntity criarPedidoEntity(UUID clienteId, UUID lojaId, UUID enderecoId, UUID pedidoId) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedidoId);
        entity.setClienteId(clienteId);
        entity.setLojaId(lojaId);
        entity.setEnderecoId(enderecoId);
        entity.setStatus(StatusPedido.CRIADO);
        entity.setTotal(BigDecimal.valueOf(25.80));
        entity.setTaxaEntrega(BigDecimal.valueOf(9.90));
        entity.setDesconto(BigDecimal.valueOf(5));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setItens(List.of());
        return entity;
    }
}
