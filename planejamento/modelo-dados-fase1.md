# Modelo de Dados – Fase 1 (MVP Core de Entrega de Pedidos)

## 1. Escopo da Fase 1 (Contexto)

A Fase 1 tem como objetivo viabilizar operação em uma região/cidade com fluxo completo:

- Cliente faz pedido em uma loja.
- Loja recebe e prepara o pedido.
- Entregador retira e entrega ao cliente.
- Pagamento é processado via um gateway principal.

O modelo de dados abaixo cobre apenas o necessário para esse fluxo **core**, deixando extensões (multi-cidade, multi-tenant avançado, analytics) para fases posteriores.

---

## 2. Entidades Essenciais e Relacionamentos

### 2.1 Visão Geral (Domínios)

- **Loja**: representa restaurantes/estabelecimentos que vendem produtos.
- **Cliente**: pessoa que faz o pedido.
- **Endereço do Cliente**: um ou mais endereços de entrega por cliente.
- **Produto**: itens vendidos pelas lojas.
- **Categoria de Produto**: organiza produtos em grupos (opcional, mas útil desde o início).
- **Pedido**: agregador principal que conecta cliente, loja, itens, pagamento e entrega.
- **Item de Pedido**: produtos específicos incluídos em um pedido.
- **Entregador**: responsável por retirar o pedido na loja e entregar ao cliente.
- **Pagamento**: informações da transação no gateway de pagamento.
- **Notificação** (log): registro de notificações enviadas (cliente, loja, entregador).

### 2.2 Diagrama Lógico (Descrição Textual)

- Uma **Loja** tem muitos **Produtos**.
- Uma **Loja** tem muitos **Pedidos**.
- Um **Cliente** tem muitos **Endereços de Cliente**.
- Um **Cliente** tem muitos **Pedidos**.
- Um **Pedido** tem muitos **Itens de Pedido**.
- Um **Pedido** se relaciona a um **Endereço de Cliente** (snapshot de entrega na criação do pedido).
- Um **Entregador** pode estar associado a muitos **Pedidos** ao longo do tempo.
- Um **Pedido** pode ter 0 ou 1 **Pagamento** (para Fase 1, 1:1 é suficiente).
- Uma **Notificação** se associa a um **Pedido** (e opcionalmente a Cliente, Loja ou Entregador).

---

## 3. Esquema Relacional Proposto (PostgreSQL)

### 3.1 Loja (`loja`)

Representa um estabelecimento (restaurante, mercado, etc.).

Campos principais:
- `id` (UUID, PK)
- `nome` (varchar)
- `documento` (varchar, opcional – CNPJ/CPF)
- `tipo_negocio` (varchar ou enum: RESTAURANTE, MERCADO, FARMACIA, etc.)
- `email_contato` (varchar, opcional)
- `telefone` (varchar, opcional)
- `ativo` (boolean)
- `horario_abertura` (time, opcional)
- `horario_fechamento` (time, opcional)
- `tempo_medio_preparo_min` (integer, opcional)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.2 Cliente (`cliente`)

Campos principais:
- `id` (UUID, PK)
- `nome` (varchar)
- `email` (varchar, opcional, único se usado para login)
- `telefone` (varchar, opcional)
- `documento` (varchar, opcional – CPF)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.3 Endereço do Cliente (`endereco_cliente`)

Um cliente pode ter múltiplos endereços. Um endereço do pedido referencia um desses endereços no momento da criação.

Campos principais:
- `id` (UUID, PK)
- `cliente_id` (UUID, FK → `cliente.id`)
- `apelido` (varchar – "Casa", "Trabalho", etc.)
- `rua` (varchar)
- `numero` (varchar)
- `complemento` (varchar, opcional)
- `bairro` (varchar)
- `cidade` (varchar)
- `estado` (varchar)
- `cep` (varchar)
- `latitude` (numeric, opcional)
- `longitude` (numeric, opcional)
- `principal` (boolean)
- `created_at` (timestamp)


### 3.4 Categoria de Produto (`categoria_produto`)

Usada para agrupar produtos da loja.

Campos principais:
- `id` (UUID, PK)
- `loja_id` (UUID, FK → `loja.id`)
- `nome` (varchar)
- `descricao` (varchar, opcional)
- `ordem_exibicao` (integer, opcional)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.5 Produto (`produto`)

Campos principais:
- `id` (UUID, PK)
- `loja_id` (UUID, FK → `loja.id`)
- `categoria_id` (UUID, FK → `categoria_produto.id`, opcional)
- `nome` (varchar)
- `descricao` (text, opcional)
- `preco` (numeric(10,2))
- `ativo` (boolean)
- `estoque_atual` (integer, opcional – pode ser nulo se não controlarmos estoque na Fase 1)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.6 Entregador (`entregador`)

Campos principais:
- `id` (UUID, PK)
- `nome` (varchar)
- `tipo` (varchar ou enum: PARCEIRO, FIXO, TERCEIRIZADO)
- `documento` (varchar, opcional – CPF/CNPJ)
- `telefone` (varchar, opcional)
- `placa_veiculo` (varchar, opcional)
- `ativo` (boolean)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.7 Pedido (`pedido`)

Entidade central do domínio.

Campos principais:
- `id` (UUID, PK)
- `cliente_id` (UUID, FK → `cliente.id`)
- `loja_id` (UUID, FK → `loja.id`)
- `endereco_entrega_id` (UUID, FK → `endereco_cliente.id`)
- `entregador_id` (UUID, FK → `entregador.id`, opcional na criação)
- `status` (enum ou varchar):
  - CRIADO
  - CONFIRMADO
  - EM_PREPARO
  - EM_ROTA
  - ENTREGUE
  - CANCELADO
- `canal` (varchar ou enum: APP, WEB, CALLCENTER, etc.)
- `valor_itens` (numeric(10,2))
- `valor_entrega` (numeric(10,2))
- `valor_desconto` (numeric(10,2))
- `valor_total` (numeric(10,2))
- `metodo_pagamento` (varchar ou enum: PIX, CARTAO, DINHEIRO)
- `status_pagamento` (varchar ou enum: PENDENTE, APROVADO, RECUSADO, ESTORNADO)
- `observacoes` (text, opcional – instruções do cliente/loja)
- `data_hora_criacao` (timestamp)
- `data_hora_confirmacao` (timestamp, opcional)
- `data_hora_saida_entrega` (timestamp, opcional)
- `data_hora_entrega` (timestamp, opcional)
- `created_at` (timestamp)
- `updated_at` (timestamp)

Observações:
- Mesmo com `status`, vale manter timestamps específicos para apoiar relatórios simples na Fase 1.
- Em fases futuras, pode-se extrair um histórico de status (`pedido_status_historico`).


### 3.8 Item de Pedido (`item_pedido`)

Representa um produto específico em um pedido.

Campos principais:
- `id` (UUID, PK)
- `pedido_id` (UUID, FK → `pedido.id`)
- `produto_id` (UUID, FK → `produto.id`)
- `nome_produto` (varchar – snapshot do nome no momento do pedido)
- `preco_unitario` (numeric(10,2) – snapshot do preço no momento do pedido)
- `quantidade` (integer)
- `observacoes` (text, opcional – ex.: "sem cebola")
- `created_at` (timestamp)


### 3.9 Pagamento (`pagamento`)

Tabela para integrar com gateway de pagamento.

Campos principais:
- `id` (UUID, PK)
- `pedido_id` (UUID, FK → `pedido.id`, único)
- `gateway` (varchar – ex.: STRIPE, PAGARME, MUNDIPAGG)
- `metodo` (varchar ou enum – PIX, CARTAO)
- `valor` (numeric(10,2))
- `moeda` (varchar, default "BRL")
- `status` (varchar ou enum: PENDENTE, APROVADO, RECUSADO, ESTORNADO)
- `transacao_id_gateway` (varchar, opcional)
- `raw_request` (jsonb, opcional – payload enviado ao gateway)
- `raw_response` (jsonb, opcional – payload retornado pelo gateway, sanitizado)
- `data_hora_autorizacao` (timestamp, opcional)
- `created_at` (timestamp)
- `updated_at` (timestamp)


### 3.10 Notificação (`notificacao`)

Log de notificações enviadas (e-mail, push, SMS) para auditoria básica.

Campos principais:
- `id` (UUID, PK)
- `pedido_id` (UUID, FK → `pedido.id`, opcional)
- `cliente_id` (UUID, FK → `cliente.id`, opcional)
- `loja_id` (UUID, FK → `loja.id`, opcional)
- `entregador_id` (UUID, FK → `entregador.id`, opcional)
- `canal` (varchar ou enum: EMAIL, PUSH, SMS, WHATSAPP)
- `tipo` (varchar – ex.: PEDIDO_CRIADO, PEDIDO_SAIU_PARA_ENTREGA)
- `destino` (varchar – e-mail, telefone, token push)
- `conteudo` (text ou jsonb, opcional)
- `status_envio` (varchar ou enum: PENDENTE, ENVIADO, FALHA)
- `mensagem_erro` (text, opcional – sem dados sensíveis)
- `created_at` (timestamp)

---

## 4. Considerações de Evolução

- **Histórico de Status de Pedido**: para a Fase 1, o status atual + timestamps em `pedido` podem ser suficientes. Em fases posteriores, criar tabela `pedido_status_historico` para análise detalhada.
- **Multi-Cidade/Multi-Região**: inicialmente podemos armazenar cidade/estado no endereço e loja. Em fases futuras, pode haver uma entidade `regiao` ou `cidade` dedicada.
- **Multi-Tenant**: neste momento, o tenant pode ser implicitamente a própria loja. Em fases futuras, podemos introduzir uma entidade `tenant` e relacioná-la às lojas.
- **Analytics**: eventos de pedido podem ser replicados para um pipeline de dados/streaming sem alterar o modelo relacional principal.

---

## 5. Próximos Passos Relacionados ao Modelo de Dados

- Refinar tipos de dados (tamanhos de campos, índices, constraints específicas) quando formos criar as migrations (Flyway/Liquibase).
- Validar o modelo com fluxos reais de negócio (ex.: cancelamento com estorno, reatribuição de entregador).
- Definir strategy de IDs (UUID vs sequences) de acordo com padrões do time.
