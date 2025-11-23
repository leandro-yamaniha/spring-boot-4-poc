# Análise de Arquiteturas – Backend de Entrega de Pedidos

## 1. Objetivo

Comparar abordagens arquiteturais relevantes para o backend:

- Arquitetura em camadas (Layered / MVC clássico).
- Clean Architecture.
- Arquitetura Hexagonal.
- CQRS.
- Uso de DDD (Domain-Driven Design) em conjunto com as opções acima.

E justificar um caminho inicial para a Fase 1 (MVP), alinhado às decisões já registradas em `decisao-stack-http-fase1.md`.

---

## 2. Arquitetura em Camadas (Layered / MVC Clássico)

### Visão Geral

- Separação típica em camadas:
  - Controller (apresentação/API).
  - Service (negócio/aplicação).
  - Repository (acesso a dados).
- Muito comum no ecossistema Spring (MVC).

### Vantagens

- Simples de entender e adotar pela maioria dos devs.
- Bem suportada por ferramentas, tutoriais e exemplos.
- Facilita onboarding e TDD básico (controllers + services + repositories).

### Desvantagens

- Com o tempo, services podem acumular muita responsabilidade ("anêmicos" ou "deus").
- Acoplamento maior entre regras de negócio e infraestrutura se não houver disciplina.

---

## 3. Clean Architecture

### Visão Geral

- Propõe círculos concêntricos de responsabilidade (domínio no centro, detalhes na borda).
- Forte separação entre regras de negócio (use cases, entities) e infraestrutura (DB, UI, frameworks).

### Vantagens

- Domínio mais isolado e testável.
- Facilita troca de tecnologias de infraestrutura.

### Desvantagens

- Curva de aprendizado maior.
- Mais camadas/abstrações podem ser demais para um MVP pequeno, se aplicadas de forma rígida.

---

## 4. Arquitetura Hexagonal

### Visão Geral

- Semelhante em espírito à Clean, com foco em **portas e adaptadores**.
- Domínio no centro, com interfaces (ports) para entrada/saída, e adapters conectando infra (DB, APIs, mensageria).

### Vantagens

- Domínio independente de detalhes técnicos.
- Facilita testes de unidade do domínio sem infraestrutura real.

### Desvantagens

- Requer disciplina para não "furar" a camada de domínio.
- Pode ser percebida como complexa em contextos de time pequeno/início de projeto.

---

## 5. CQRS (Command Query Responsibility Segregation)

### Visão Geral

- Separação explícita entre modelo de escrita (commands) e leitura (queries).
- Muitas vezes associado a event sourcing, mas não obrigatoriamente.

### Vantagens

- Otimização independente para leitura e escrita.
- Pode simplificar queries complexas em domínios ricos.

### Desvantagens

- Aumenta a complexidade arquitetural (dois modelos, sincronização, consistência eventual em alguns cenários).
- Pode ser overkill em um MVP com volume moderado e domínio ainda em amadurecimento.

---

## 6. DDD (Domain-Driven Design)

### Visão Geral

- Foco em modelar o **domínio de negócio** com linguagem ubíqua.
- Uso de padrões táticos: entidades ricas, agregados, value objects, repositórios, domínios de serviço, etc.
- Pode ser aplicado sobre diferentes estilos arquiteturais (layered, hexagonal, clean).

### Vantagens

- Maior alinhamento entre código e negócio.
- Facilita evolução do sistema conforme o entendimento do domínio aumenta.

### Desvantagens

- Curva de aprendizado; exige proximidade com o negócio.
- Pode ser pesado se o domínio for muito simples (não é o caso aqui, pois o domínio de pedidos/entrega tende a crescer).

---

## 7. Caminho Inicial para este Projeto

Considerando:

- Escopo e complexidade esperados do domínio de **pedidos/entregas**.
- Objetivo de ter um **MVP Fase 1** relativamente rápido, mas com base saudável para evoluir.
- Time trabalhando com Java 25 + Spring Boot 4.x, com forte ênfase em legibilidade e TDD.

Decisão inicial:

- Adotar uma **arquitetura em camadas inspirada em princípios de DDD e Hexagonal**, sem implementar um CQRS completo neste momento.
  - Camadas principais:
    - **API** (`...api`): controllers REST, DTOs, mapeamento de entrada/saída.
    - **Aplicação** (`...application`): casos de uso/serviços de aplicação, orquestrando o domínio.
    - **Domínio** (`...domain`): entidades de domínio, agregados, value objects, regras de negócio.
    - **Infraestrutura** (`...infrastructure`): persistência (JPA/Repositories), integrações externas, mapeadores para DB.
  - Utilizar **ports & adapters** de forma leve onde fizer sentido (por exemplo, interfaces de repositório no domínio/aplicação com implementações em infraestrutura).
- Não adotar CQRS completo na Fase 1:
  - Manter leitura/escrita no mesmo modelo por enquanto.
  - Deixar CQRS como opção futura, caso surjam requisitos de escala/performance/leitura complexa.

---

## 8. Relação com o ADR da Stack HTTP

- O ADR `decisao-stack-http-fase1.md` já define:
  - Uso de **Spring MVC/Web** (não reativo) com **virtual threads**.
  - Java 25 LTS + Spring Boot 4.x.
  - Uso de `@ControllerAdvice/@RestControllerAdvice` para tratamento centralizado de erros.
- Este documento complementa o ADR ao definir o **estilo arquitetural interno** da aplicação (camadas + DDD leve + princípios hexagonais), sem alterar a decisão de stack HTTP.

---

## 9. Próximos Passos

- Refletir esta organização de camadas e contexto de DDD na criação do scaffold (HIST-000).
- Ao modelar o domínio de pedidos, identificar possíveis agregados (ex.: `Pedido` como agregado raiz com itens).
- Se, em fases futuras, o volume/complexidade justificar, considerar:
  - Introduzir CQRS em áreas específicas (por exemplo, relatórios/consultas analíticas).
  - Aumentar o uso de ports & adapters e, se necessário, modularizar o projeto.
