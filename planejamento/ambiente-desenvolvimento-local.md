# Ambiente de Desenvolvimento Local – Backend de Entrega de Pedidos

## 1. Objetivo

Descrever como será o **ambiente local de desenvolvimento** utilizando **containers** (Docker/Docker Compose), permitindo que qualquer pessoa do time consiga:

- Subir rapidamente os serviços de infraestrutura (banco, cache, etc.).
- Rodar o backend localmente (na IDE) ou em container.
- Testar a aplicação de ponta a ponta em ambiente previsível e próximo de produção.

---

## 2. Componentes do Ambiente Local

Serviços mínimos previstos para o ambiente local:

- **backend-app**
  - Aplicação Spring Boot 4.x (Java 25 LTS).
  - Exposição da API HTTP (porta configurável, ex.: 8080).

- **postgres**
  - Banco de dados PostgreSQL.
  - Database dedicado ao ambiente local (por exemplo, `orders_local`).

- **redis**
  - Instância Redis para cache.

Outros serviços podem ser adicionados futuramente (mensageria, ferramentas de observabilidade, etc.).

---

## 3. Estratégia de Execução

### 3.1. Backend local + infraestrutura em containers

Cenário comum de desenvolvimento:

- O desenvolvedor roda o backend na própria IDE (perfil local/dev).
- Banco, Redis (e demais serviços) sobem via Docker Compose.

Benefícios:

- Hot reload/depuração facilitada na IDE.
- Infraestrutura reprodutível e descartável via containers.

### 3.2. Backend em container + infraestrutura em containers

Cenário para testes mais próximos de produção:

- Backend também empacotado em imagem Docker.
- Compose sobe: backend + PostgreSQL + Redis.

Uso típico:

- Validação de configuração de imagem/container.
- Testes manuais ou automáticos end-to-end em ambiente 100% containerizado.

---

## 4. Docker Compose (Ambiente Local)

O arquivo [`infra/local/docker-compose.yml`](../infra/local/docker-compose.yml) define os serviços mínimos para o ambiente local. Ele contém, no mínimo, serviços como:

- Serviço `backend-app`:
  - Build da imagem a partir do código do projeto (Gradle + Dockerfile).
  - Variáveis de ambiente apontando para o banco e Redis.

- Serviço `postgres`:
  - Imagem oficial do PostgreSQL.
  - Volumes para persistência local de dados (quando desejado).
  - Configuração de usuário/senha/banco para ambiente de dev.

- Serviço `redis`:
  - Imagem oficial do Redis.
  - Configuração simples para cache local.

A definição detalhada (nomes de serviços, portas e volumes) será feita quando começarmos a implementar o backend.

---

## 5. Integração com Testes

- **Testcontainers** permanecerá como solução preferencial para testes automatizados (unitários/integrados) durante o build, criando containers efêmeros para banco/serviços.
- O ambiente local Docker/Docker Compose serve para:
  - Desenvolvimento manual e testes exploratórios.
  - Execução local da aplicação como se estivesse em um ambiente reduzido de produção.

---

## 6. Próximos Passos

- Definir a estrutura concreta do `docker-compose.yml` para o backend (serviços, imagens, volumes, variáveis de ambiente).
- Padronizar perfis Spring (`local`, `dev`, `test`) para conectar ao ambiente apropriado (containers de dev, Testcontainers em testes, etc.).
- Integrar os comandos de `docker compose` à documentação de execução local (README) quando o backend estiver criado.
