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

Com o container `postgres` em execução e o perfil `local` ativo, é possível:

- Rodar a aplicação pela IDE (`bootRun` ou configuração própria).
- Realizar testes funcionais manuais chamando os endpoints HTTP (por exemplo, `POST /api/v1/orders`).
