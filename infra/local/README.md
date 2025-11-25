# Ambiente Local de Infra – Backend de Entrega

Este diretório contém a definição de infraestrutura mínima para desenvolvimento local e testes funcionais manuais da aplicação.

## Componentes

- **postgres**
  - Imagem: `postgres:16-alpine`
  - Database: `delivery`
  - Usuário: `delivery`
  - Senha: `delivery`
  - Porta exposta: `5432` (host) → `5432` (container)

## Subir infraestrutura local

No diretório raiz do projeto (`spring-boot-4-poc`):

```bash
docker compose -f infra/local/docker-compose.yml up -d
```

Isso irá:

- Criar um container `delivery-postgres-local` com PostgreSQL 16.
- Expor a porta `5432` no host.
- Criar um volume nomeado `delivery_postgres_data` para persistência opcional de dados.
- Executar as migrations Flyway automaticamente.

Para verificar se o banco está no ar:

```bash
docker ps
# ou
docker logs delivery-postgres-local
```

## Parar e limpar infraestrutura

```bash
docker compose -f infra/local/docker-compose.yml down
```

Para remover também o volume de dados (reset completo do banco local):

```bash
docker compose -f infra/local/docker-compose.yml down -v
```

## Configuração da aplicação (perfil `local`)

Para a aplicação Spring Boot rodar contra esse banco em ambiente local, o perfil `local` deve apontar para:

- **URL JDBC**: `jdbc:postgresql://localhost:5432/delivery`
- **Usuário**: `delivery`
- **Senha**: `delivery`

Exemplo conceitual (dentro de `application-local.yml`):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/delivery
    username: delivery
    password: delivery
    driver-class-name: org.postgresql.Driver
```

## Testando a API - HIST-001 ✅

Com o container `postgres` em execução e o perfil `local` ativo, é possível testar a **HIST-001: Criação de Pedido**:

### 1. Iniciar a aplicação

```bash
# No diretório raiz
./gradlew bootRun --args='--spring.profiles.active=local'
```

A aplicação estará disponível em: **[http://localhost:8080](http://localhost:8080)**

### 2. Criar um pedido (exemplo funcional)

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": "550e8400-e29b-41d4-a716-446655440000",
    "lojaId": "660e8400-e29b-41d4-a716-446655440001",
    "enderecoId": "770e8400-e29b-41d4-a716-446655440002",
    "itens": [
      {
        "produtoId": "880e8400-e29b-41d4-a716-446655440003",
        "quantidade": 2,
        "precoUnitario": 25.00,
        "observacoes": "Sem cebola"
      }
    ]
  }'
```

**Nota**: Este exemplo usa UUIDs fictícios. Para testes reais, seria necessário popular o banco com dados de teste.

### 3. Verificar resposta esperada

```json
{
  "id": "uuid-gerado",
  "clienteId": "550e8400-e29b-41d4-a716-446655440000",
  "lojaId": "660e8400-e29b-41d4-a716-446655440001",
  "enderecoId": "770e8400-e29b-41d4-a716-446655440002",
  "itens": [
    {
      "id": "uuid-item",
      "produtoId": "880e8400-e29b-41d4-a716-446655440003",
      "produtoNome": "Produto 880e8400-e29b-41d4-a716-446655440003",
      "quantidade": 2,
      "precoUnitario": 25.00,
      "subtotal": 50.00
    }
  ],
  "status": "CRIADO",
  "subtotal": 50.00,
  "taxaEntrega": 9.90,
  "desconto": 0.00,
  "total": 59.90,
  "createdAt": "2025-11-24T22:30:00Z",
  "updatedAt": "2025-11-24T22:30:00Z"
}
```

### 4. Testar cenários de erro

```bash
# Pedido sem itens
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"clienteId": "uuid", "lojaId": "uuid", "enderecoId": "uuid", "itens": []}'
# Status: 400 Bad Request

# Cliente sem ID
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"lojaId": "uuid", "enderecoId": "uuid", "itens": [{"produtoId": "uuid", "quantidade": 1, "precoUnitario": 10.0}]}'
# Status: 400 Bad Request
```

### 5. Verificar logs da aplicação

```bash
# Logs de requisição (HTTP-001)
# Logs de criação de pedido (ORD-001)
# Logs de validação (ORD-010 se houver erro)
```

### 6. Documentação da API

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Actuator**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Próximos Passos

Após validar o funcionamento da HIST-001, considere implementar as melhorias futuras:

- **HIST-004**: Controle de estoque e disponibilidade
- **HIST-006**: Segurança e rate limiting
- **HIST-005**: Observabilidade e métricas

Para mais detalhes, consulte o [CHANGELOG.md](../../CHANGELOG.md) e as [issues no GitHub](https://github.com/leandro-yamaniha/spring-boot-4-poc/issues).
