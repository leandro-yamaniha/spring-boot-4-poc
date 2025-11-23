# Kanboard – Instalação e Uso Local

## 1. Objetivo

Documentar um **checklist simples** para instalar e rodar o **Kanboard** localmente, e configurá-lo como ferramenta oficial de gestão de tarefas do projeto de **backend de entrega de pedidos**.

Foco:

- Rodar Kanboard localmente (preferencialmente via Docker).
- Criar um projeto específico para este backend.
- Alinhar colunas, swimlanes e tags ao que foi descrito em `agilidade-gestao-tarefas.md`.

---

## 2. Pré-requisitos

- Máquina de desenvolvimento (macOS, Linux ou Windows).
- Acesso à internet para baixar a imagem do Kanboard.
- **Recomendado**: Docker ou Docker Desktop instalado.

Se você não puder usar Docker, ainda é possível instalar Kanboard em um servidor PHP/Apache ou Nginx – ver seção "Opção 2".

---

## 3. Opção 1 – Rodando Kanboard com Docker (Recomendado)

### 3.1. Criar volumes para dados e plugins

No terminal:

```bash
docker volume create kanboard_data
docker volume create kanboard_plugins
```

### 3.2. (Opcional) Criar uma rede dedicada para o Kanboard

```bash
docker network create kanboard_net
```

### 3.3. Subir o container Kanboard

Com Docker instalado, execute:

```bash
docker run -d \
  --name kanboard \
  -p 8081:80 \
  -v kanboard_data:/var/www/app/data \
  -v kanboard_plugins:/var/www/app/plugins \
  --network kanboard_net \
  kanboard/kanboard:latest
```

Notas:

- A aplicação ficará disponível em: `http://localhost:8081`.
- Os dados (projetos, tarefas, usuários) ficarão persistidos no volume `kanboard_data`.
- Plugins opcionalmente ficarão em `kanboard_plugins`.

### 3.4. Primeiro acesso

1. Abra `http://localhost:8081` no navegador.
2. Credenciais padrão (conforme documentação do Kanboard):
   - Usuário: `admin`
   - Senha: `admin`
3. **Altere a senha do usuário `admin`** imediatamente em "Meu perfil" → "Alterar senha".

### 3.5. Parar e iniciar o Kanboard

- **Parar**:

```bash
docker stop kanboard
```

- **Iniciar novamente**:

```bash
docker start kanboard
```

- **Remover container (mantendo dados)**:

```bash
docker rm kanboard
```

Você pode recriar o container depois, reaproveitando os volumes `kanboard_data` e `kanboard_plugins`.

### 3.6. Usando o docker-compose deste repositório

Como alternativa ao comando `docker run`, este repositório já inclui um arquivo de compose em `planejamento/agilidade/docker-compose.kanboard.yml`.

A partir da raiz do projeto (`spring-boot-4-poc`), você pode subir o Kanboard com:

```bash
docker compose -f planejamento/agilidade/docker-compose.kanboard.yml up -d
```

Para derrubar o serviço (sem apagar os volumes/persistência de dados):

```bash
docker compose -f planejamento/agilidade/docker-compose.kanboard.yml down
```

Os volumes nomeados `kanboard_data` e `kanboard_plugins` garantem que as configurações, projetos e tarefas sejam preservados entre recriações de container.

---

## 4. Opção 2 – Instalação sem Docker (PHP + Web Server)

Caso você não possa usar Docker, o fluxo geral é:

1. Instalar **PHP** (versão suportada pelo Kanboard) e extensões necessárias.
2. Instalar **Apache** ou **Nginx**.
3. Baixar o Kanboard a partir de:
   - Site oficial: <https://kanboard.org/>
   - Documentação oficial: <https://docs.kanboard.org/>
4. Configurar o diretório do Kanboard como *DocumentRoot* (Apache) ou *server root* (Nginx).
5. Ajustar permissões de escrita na pasta `data/` do Kanboard.

Para detalhes, siga as instruções oficiais de instalação na documentação do Kanboard.

---

## 5. Configuração do Projeto no Kanboard

Depois de Kanboard estar rodando (via Docker ou instalação manual), siga este checklist:

1. **Criar projeto**
   - Nome sugerido: `Backend – Entrega de Pedidos`.
   - (Opcional) Uma descrição com link para o repositório Git local/remoto.

2. **Configurar colunas (Kanban)**
   - Backlog
   - To Do
   - In Progress
   - Code Review
   - Testing
   - Done

3. **Configurar swimlanes (opcional, mas recomendado)**
   - Fase 1 – MVP Core de Pedidos
   - Fase 2 – Entregadores & Operação
   - Fase 3 – Multi-tenant
   - Fase 4 – Analytics & Otimização

4. **Criar tags úteis**
   - `dominio-pedido`
   - `dominio-loja`
   - `dominio-cliente`
   - `pagamento`
   - `infra`
   - `observabilidade`

5. **Criar primeiros cartões (tarefas/épicos)**
   - Épico: "Fase 1 – MVP Core de Pedidos".
   - Tarefas técnicas:
     - "Modelo de dados Fase 1" (já iniciado na pasta `planejamento`).
     - "Endpoints REST Fase 1".
     - "Requisitos não funcionais Fase 1".
   - Histórias de usuário, por exemplo:
     - "Como cliente, quero criar um pedido".
     - "Como loja, quero visualizar os pedidos em tempo real".

---

## 6. Integração Conceitual com Git

Mesmo sem integração automatizada entre Kanboard e Git, podemos padronizar o vínculo:

1. **IDs de tarefa do Kanboard**
   - Cada cartão tem um ID (ex.: `#12`).

2. **Branches nomeados com o ID**
   - Ex.: `feature/KAN-12-criacao-pedido` ou `feature/card-12-criacao-pedido`.

3. **Mensagens de commit citando o ID**
   - Ex.: `feat: cria endpoint de criacao de pedido (card #12)`.

4. **Fluxo de trabalho**
   - Mover o cartão em Kanboard conforme o status real do desenvolvimento: Backlog → To Do → In Progress → Code Review → Testing → Done.

---

## 7. Checklist Resumido

- [ ] Instalar Docker (se ainda não estiver instalado).
- [ ] Criar volumes Docker `kanboard_data` e `kanboard_plugins`.
- [ ] Subir o container `kanboard/kanboard` expondo porta 8081.
- [ ] Acessar `http://localhost:8081` e alterar senha do `admin`.
- [ ] Criar projeto `Backend – Entrega de Pedidos` no Kanboard.
- [ ] Configurar colunas Kanban (Backlog, To Do, In Progress, Code Review, Testing, Done).
- [ ] Configurar swimlanes por fase (Fase 1, Fase 2, ...).
- [ ] Criar tags de domínio (`dominio-pedido`, `infra`, etc.).
- [ ] Criar épico e tarefas iniciais da Fase 1.
- [ ] Padronizar uso de IDs de cartão nas branches e mensagens de commit.

---

## 8. Documentação da API do Kanboard

Para integrações via API (por exemplo, JSON-RPC), consulte a documentação oficial:

- <https://docs.kanboard.org/v1/api/>
