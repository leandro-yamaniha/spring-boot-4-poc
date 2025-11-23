package com.poc.delivery.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
    
    private UUID id;
    private final UUID clienteId;
    private final UUID lojaId;
    private final UUID enderecoId;
    private final List<ItemDePedido> itens;
    private StatusPedido status;
    private BigDecimal total;
    private BigDecimal taxaEntrega;
    private BigDecimal desconto;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Pedido(Builder builder) {
        this.clienteId = Objects.requireNonNull(builder.clienteId, "clienteId não pode ser nulo");
        this.lojaId = Objects.requireNonNull(builder.lojaId, "lojaId não pode ser nulo");
        this.enderecoId = Objects.requireNonNull(builder.enderecoId, "enderecoId não pode ser nulo");
        this.itens = new ArrayList<>(Objects.requireNonNull(builder.itens, "itens não podem ser nulos"));
        this.status = StatusPedido.CRIADO;
        this.taxaEntrega = builder.taxaEntrega != null ? builder.taxaEntrega : BigDecimal.ZERO;
        this.desconto = builder.desconto != null ? builder.desconto : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        validar();
        this.total = calcularTotal();
    }
    
    private void validar() {
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
        }
        if (taxaEntrega.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }
        if (desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Desconto não pode ser negativo");
        }
    }
    
    private BigDecimal calcularTotal() {
        BigDecimal subtotalItens = itens.stream()
            .map(ItemDePedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return subtotalItens
            .add(taxaEntrega)
            .subtract(desconto);
    }
    
    public void confirmar() {
        if (status != StatusPedido.CRIADO) {
            throw new IllegalStateException("Apenas pedidos criados podem ser confirmados");
        }
        this.status = StatusPedido.CONFIRMADO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancelar() {
        if (status == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Pedido já entregue não pode ser cancelado");
        }
        if (status == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido já está cancelado");
        }
        this.status = StatusPedido.CANCELADO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void marcarComoPronto() {
        if (status != StatusPedido.CONFIRMADO) {
            throw new IllegalStateException("Apenas pedidos confirmados podem ser marcados como prontos");
        }
        this.status = StatusPedido.PRONTO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void iniciarEntrega() {
        if (status != StatusPedido.PRONTO) {
            throw new IllegalStateException("Apenas pedidos prontos podem iniciar entrega");
        }
        this.status = StatusPedido.EM_ENTREGA;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void finalizarEntrega() {
        if (status != StatusPedido.EM_ENTREGA) {
            throw new IllegalStateException("Apenas pedidos em entrega podem ser finalizados");
        }
        this.status = StatusPedido.ENTREGUE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }
    
    public UUID getClienteId() {
        return clienteId;
    }
    
    public UUID getLojaId() {
        return lojaId;
    }
    
    public UUID getEnderecoId() {
        return enderecoId;
    }
    
    public List<ItemDePedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
    
    public StatusPedido getStatus() {
        return status;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }
    
    public BigDecimal getDesconto() {
        return desconto;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
               "id=" + id +
               ", clienteId=" + clienteId +
               ", lojaId=" + lojaId +
               ", status=" + status +
               ", total=" + total +
               ", createdAt=" + createdAt +
               '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID clienteId;
        private UUID lojaId;
        private UUID enderecoId;
        private List<ItemDePedido> itens = new ArrayList<>();
        private BigDecimal taxaEntrega;
        private BigDecimal desconto;
        
        public Builder clienteId(UUID clienteId) {
            this.clienteId = clienteId;
            return this;
        }
        
        public Builder lojaId(UUID lojaId) {
            this.lojaId = lojaId;
            return this;
        }
        
        public Builder enderecoId(UUID enderecoId) {
            this.enderecoId = enderecoId;
            return this;
        }
        
        public Builder itens(List<ItemDePedido> itens) {
            this.itens = itens;
            return this;
        }
        
        public Builder adicionarItem(ItemDePedido item) {
            this.itens.add(item);
            return this;
        }
        
        public Builder taxaEntrega(BigDecimal taxaEntrega) {
            this.taxaEntrega = taxaEntrega;
            return this;
        }
        
        public Builder desconto(BigDecimal desconto) {
            this.desconto = desconto;
            return this;
        }
        
        public Pedido build() {
            return new Pedido(this);
        }
    }
}
