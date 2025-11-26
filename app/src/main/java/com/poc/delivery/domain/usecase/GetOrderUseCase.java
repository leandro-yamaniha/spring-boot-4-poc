package com.poc.delivery.domain.usecase;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.poc.delivery.api.dto.ItemPedidoResponse;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.common.logging.LogEvent;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

@Component
public class GetOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrderUseCase.class);

    private final PedidoRepository repository;
    private final PedidoMapper mapper;

    public GetOrderUseCase(PedidoRepository repository, PedidoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PedidoResponse execute(UUID pedidoId) {
        LOGGER.info("[{}] Buscando pedido com id={}", LogEvent.ORDER_RETRIEVED.code(), pedidoId);

        PedidoEntity entity = repository.findById(pedidoId)
            .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado: " + pedidoId));

        Pedido pedido = mapper.toDomain(entity);

        LOGGER.info("[{}] Pedido encontrado com sucesso, id={}", LogEvent.ORDER_RETRIEVED.code(), pedidoId);

        return toResponse(pedido);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itensResponse = pedido.getItens().stream()
            .map(item -> new ItemPedidoResponse(
                item.getProdutoId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal(),
                item.getObservacoes()
            ))
            .toList();

        return new PedidoResponse(
            pedido.getId(),
            pedido.getClienteId(),
            pedido.getLojaId(),
            pedido.getEnderecoId(),
            itensResponse,
            pedido.getStatus(),
            pedido.getTotal(),
            pedido.getTaxaEntrega(),
            pedido.getDesconto(),
            pedido.getCreatedAt(),
            pedido.getUpdatedAt()
        );
    }
}
