package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.ItemPedidoResponse;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.common.logging.LogEvent;
import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

@Component
public class CreateOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrderUseCase.class);
    
    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final OrderValidator validator;
    private final PriceCalculator priceCalculator;
    
    public CreateOrderUseCase(
        PedidoRepository repository,
        PedidoMapper mapper,
        OrderValidator validator,
        PriceCalculator priceCalculator
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.priceCalculator = priceCalculator;
    }
    
    @Transactional
    public PedidoResponse execute(PedidoRequest request) {
        validator.validate(request);

        List<ItemDePedido> itens = request.itens().stream()
            .map(this::toItemDomain)
            .toList();

        BigDecimal taxaEntrega = priceCalculator.calculateTaxaEntrega(itens);
        BigDecimal desconto = priceCalculator.calculateDesconto(itens);
        
        Pedido pedido = Pedido.builder()
            .clienteId(request.clienteId())
            .lojaId(request.lojaId())
            .enderecoId(request.enderecoId())
            .itens(itens)
            .taxaEntrega(taxaEntrega)
            .desconto(desconto)
            .build();
        
        PedidoEntity entity = mapper.toEntity(pedido);
        PedidoEntity saved = repository.save(entity);
        Pedido savedPedido = mapper.toDomain(saved);

        LOGGER.info("[{}] Pedido persistido com id={}", LogEvent.ORDER_CREATED.code(), savedPedido.getId());

        return toResponse(savedPedido);
    }
    
    private ItemDePedido toItemDomain(ItemPedidoRequest request) {
        return ItemDePedido.builder()
            .produtoId(request.produtoId())
            .nomeProduto("Produto " + request.produtoId())
            .quantidade(request.quantidade())
            .precoUnitario(request.precoUnitario())
            .observacoes(request.observacoes())
            .build();
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
