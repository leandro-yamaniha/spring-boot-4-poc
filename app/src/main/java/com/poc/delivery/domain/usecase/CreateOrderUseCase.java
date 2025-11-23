package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.List;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.ItemPedidoResponse;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.domain.model.ItemDePedido;
import com.poc.delivery.domain.model.Pedido;
import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;
import com.poc.delivery.infrastructure.persistence.mapper.PedidoMapper;
import com.poc.delivery.infrastructure.persistence.repository.PedidoRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateOrderUseCase {
    
    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    
    public CreateOrderUseCase(PedidoRepository repository, PedidoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Transactional
    public PedidoResponse execute(PedidoRequest request) {
        List<ItemDePedido> itens = request.itens().stream()
            .map(this::toItemDomain)
            .toList();
        
        Pedido pedido = Pedido.builder()
            .clienteId(request.clienteId())
            .lojaId(request.lojaId())
            .enderecoId(request.enderecoId())
            .itens(itens)
            .taxaEntrega(BigDecimal.ZERO)
            .desconto(BigDecimal.ZERO)
            .build();
        
        PedidoEntity entity = mapper.toEntity(pedido);
        PedidoEntity saved = repository.save(entity);
        Pedido savedPedido = mapper.toDomain(saved);
        
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
