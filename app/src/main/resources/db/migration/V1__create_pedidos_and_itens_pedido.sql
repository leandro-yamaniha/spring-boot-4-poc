CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    loja_id UUID NOT NULL,
    endereco_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    total NUMERIC(10, 2) NOT NULL,
    taxa_entrega NUMERIC(10, 2) NOT NULL,
    desconto NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE itens_pedido (
    pedido_id UUID NOT NULL,
    produto_id UUID NOT NULL,
    nome_produto VARCHAR(200) NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    observacoes VARCHAR(500),
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE INDEX idx_pedidos_cliente_id ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_loja_id ON pedidos(loja_id);
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_created_at ON pedidos(created_at);
CREATE INDEX idx_itens_pedido_pedido_id ON itens_pedido(pedido_id);
