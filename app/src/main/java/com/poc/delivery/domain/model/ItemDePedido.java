package com.poc.delivery.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ItemDePedido {
    
    private final UUID produtoId;
    private final String nomeProduto;
    private final Integer quantidade;
    private final BigDecimal precoUnitario;
    private final BigDecimal subtotal;
    private final String observacoes;
    
    private ItemDePedido(Builder builder) {
        this.produtoId = Objects.requireNonNull(builder.produtoId, "produtoId não pode ser nulo");
        this.nomeProduto = Objects.requireNonNull(builder.nomeProduto, "nomeProduto não pode ser nulo");
        this.quantidade = Objects.requireNonNull(builder.quantidade, "quantidade não pode ser nula");
        this.precoUnitario = Objects.requireNonNull(builder.precoUnitario, "precoUnitario não pode ser nulo");
        this.observacoes = builder.observacoes;
        
        validar();
        this.subtotal = calcularSubtotal();
    }
    
    private void validar() {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
        if (nomeProduto.isBlank()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
    }
    
    private BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
    
    public UUID getProdutoId() {
        return produtoId;
    }
    
    public String getNomeProduto() {
        return nomeProduto;
    }
    
    public Integer getQuantidade() {
        return quantidade;
    }
    
    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemDePedido that = (ItemDePedido) o;
        return Objects.equals(produtoId, that.produtoId) &&
               Objects.equals(nomeProduto, that.nomeProduto) &&
               Objects.equals(quantidade, that.quantidade) &&
               Objects.equals(precoUnitario, that.precoUnitario) &&
               Objects.equals(observacoes, that.observacoes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(produtoId, nomeProduto, quantidade, precoUnitario, observacoes);
    }
    
    @Override
    public String toString() {
        return "ItemDePedido{" +
               "produtoId=" + produtoId +
               ", nomeProduto='" + nomeProduto + '\'' +
               ", quantidade=" + quantidade +
               ", precoUnitario=" + precoUnitario +
               ", subtotal=" + subtotal +
               ", observacoes='" + observacoes + '\'' +
               '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID produtoId;
        private String nomeProduto;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private String observacoes;
        
        public Builder produtoId(UUID produtoId) {
            this.produtoId = produtoId;
            return this;
        }
        
        public Builder nomeProduto(String nomeProduto) {
            this.nomeProduto = nomeProduto;
            return this;
        }
        
        public Builder quantidade(Integer quantidade) {
            this.quantidade = quantidade;
            return this;
        }
        
        public Builder precoUnitario(BigDecimal precoUnitario) {
            this.precoUnitario = precoUnitario;
            return this;
        }
        
        public Builder observacoes(String observacoes) {
            this.observacoes = observacoes;
            return this;
        }
        
        public ItemDePedido build() {
            return new ItemDePedido(this);
        }
    }
}
