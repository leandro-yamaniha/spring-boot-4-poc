package com.poc.delivery.domain.model;

/**
 * Status possíveis de um pedido no sistema.
 * Representa o ciclo de vida de um pedido desde sua criação até conclusão.
 */
public enum StatusPedido {
    /**
     * Pedido criado, aguardando confirmação da loja
     */
    CRIADO,
    
    /**
     * Pedido confirmado pela loja, em preparação
     */
    CONFIRMADO,
    
    /**
     * Pedido pronto para entrega
     */
    PRONTO,
    
    /**
     * Pedido em rota de entrega
     */
    EM_ENTREGA,
    
    /**
     * Pedido entregue ao cliente
     */
    ENTREGUE,
    
    /**
     * Pedido cancelado
     */
    CANCELADO
}
