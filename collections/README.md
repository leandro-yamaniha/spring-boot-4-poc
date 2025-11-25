# Collections de Requisições HTTP - Bruno

Este diretório contém a collection **Bruno** para testar os endpoints da aplicação.

## 🚀 Por que Bruno?

- ✅ **Git-friendly**: arquivos `.bru` em texto puro, fáceis de versionar
- ✅ **Open Source**: sem vendor lock-in
- ✅ **Leve e rápido**: não requer conta ou sincronização em nuvem
- ✅ **Offline-first**: funciona 100% local
- ✅ **Moderno**: interface limpa e intuitiva

## 📁 Estrutura

```
collections/
├── bruno.json                    # Configuração da collection
├── environments/
│   └── Local.bru                 # Ambiente local (base_url)
├── Actuator/
│   ├── Health Check.bru
│   ├── Health - Liveness.bru
│   ├── Health - Readiness.bru
│   └── Application Info.bru
├── Orders/                       # HIST-001: Criação de pedidos
│   ├── Criar Pedido - Exemplo Completo.bru
│   ├── Criar Pedido - Frete Grátis.bru
│   ├── Criar Pedido - Com Desconto.bru
│   ├── Criar Pedido - Mínimo.bru
│   └── Criar Pedido - Erro Validação.bru
└── API Documentation/
    ├── OpenAPI Spec (JSON).bru
    ├── OpenAPI Spec (YAML).bru
    └── Swagger UI.bru
```

## 🔧 Como usar

### 1. Instalar Bruno

**macOS (Homebrew)**:
```bash
brew install bruno
```

**Linux**:
```bash
# Baixe o .AppImage ou .deb de https://www.usebruno.com/downloads
```

**Windows**:
```bash
# Baixe o instalador de https://www.usebruno.com/downloads
```

### 2. Abrir a Collection

1. Abra o **Bruno**
2. Clique em **Open Collection**
3. Selecione a pasta `collections/` deste repositório
4. Pronto! Todas as requisições estarão disponíveis

### 3. Selecionar o Environment

1. No canto superior direito, selecione **Local**
2. A variável `base_url` será configurada para `http://localhost:8080`

### 4. Executar Requisições

1. Navegue pela árvore de pastas (Actuator, Orders, API Documentation, etc.)
2. Clique em uma requisição
3. Clique em **Send** ou pressione `Ctrl+Enter` / `Cmd+Enter`
4. Veja a resposta no painel direito

#### 📋 Testes Disponíveis

**Orders** (HIST-001):
- **Criar Pedido - Exemplo Completo**: Pedido com múltiplos itens e observações
- **Criar Pedido - Frete Grátis**: Pedido ≥ R$ 100 (sem frete)
- **Criar Pedido - Com Desconto**: Pedido com 3+ itens (5% desconto)
- **Criar Pedido - Mínimo**: Pedido mais simples possível
- **Criar Pedido - Erro Validação**: Teste de campos obrigatórios

**Actuator**:
- Health checks e informações da aplicação

**API Documentation**:
- OpenAPI specs e interface Swagger

## 🌍 Ambientes

### Local (padrão)
- `base_url`: `http://localhost:8080`

Para adicionar novos ambientes (dev, staging, prod), crie arquivos `.bru` em `environments/`:

```
vars {
  base_url: https://api.dev.example.com
}
```

## 📝 Adicionar novas requisições

Você pode criar requisições diretamente no Bruno ou criar arquivos `.bru` manualmente:

```
meta {
  name: Nome da Requisição
  type: http
  seq: 1
}

get {
  url: {{base_url}}/endpoint
  body: none
  auth: none
}

headers {
  Accept: application/json
}

docs {
  Documentação da requisição
}
```

## 🔗 Referências

- **Bruno**: <https://www.usebruno.com/>
- **Documentação**: <https://docs.usebruno.com/>
- **GitHub**: <https://github.com/usebruno/bruno>
