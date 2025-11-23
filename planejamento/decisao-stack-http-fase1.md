# Decisão Arquitetural – Stack HTTP Fase 1

## 1. Contexto

A Fase 1 do backend de entrega de pedidos terá:

- Volume alvo inicial: ~5k pedidos/dia em 1 cidade.
- Pico esperado: 20–30 requisições/segundo.
- Banco de dados relacional (PostgreSQL) com JPA/JDBC.
- Integrações HTTP externas (gateway de pagamento, notificações, etc.).
- Prioridade forte em legibilidade, TDD e simplicidade de manutenção (ver `agents.md`).

Foi avaliada a escolha entre uma stack **reativa (Spring WebFlux)** e uma stack **não reativa (Spring MVC/Spring Web) usando virtual threads**.

## 2. Opção A – Stack Reativa (Spring WebFlux)

- **Descrição**
  - Uso de Spring WebFlux, programação reativa (`Mono`/`Flux`), drivers reativos (R2DBC), WebClient reativo, etc.

- **Vantagens esperadas**
  - Alta escalabilidade I/O-bound com poucos threads físicos.
  - Suporte nativo a streams reativos e backpressure.
  - Bom encaixe para cargas com muitas conexões simultâneas e long-lived (SSE/WebSocket).

- **Desvantagens / riscos no contexto atual**
  - Maior complexidade de modelo mental (`Mono`/`Flux`, operadores reativos).
  - Código menos trivial para TDD, debug e onboarding de novos devs.
  - Exige ecosistema todo reativo (DB, HTTP clients); uso de libs bloqueantes degrada benefícios.
  - Potencialmente overkill para a carga e perfil de uso previstos na Fase 1.

## 3. Opção B – Stack Não Reativa + Virtual Threads

- **Descrição**
  - Uso de Spring MVC/Spring Web (modelo clássico bloqueante), com:
    - Controladores síncronos (`@RestController`).
    - JPA/JDBC para acesso a banco.
    - Clientes HTTP tradicionais.
  - Execução de cada requisição em **virtual threads** (Projeto Loom), permitindo milhares de requisições concorrentes com modelo imperativo simples.

- **Vantagens esperadas**
  - Modelo de programação imperativo, mais simples de ler, testar e revisar.
  - Reaproveitamento de todo o ecossistema de bibliotecas bloqueantes (JDBC, SDKs de cloud, etc.).
  - Benefício de concorrência elevada por conta das virtual threads, sem a complexidade do modelo reativo.
  - Alinhamento com os princípios do `agents.md` (legibilidade, TDD, clareza de responsabilidades).

- **Desvantagens / riscos**
  - Virtual threads são uma tecnologia relativamente recente; exigem atenção às versões de Java/Spring suportadas.
  - Ganho limitado em cenários puramente CPU-bound (onde a contenção é CPU, não I/O).

## 4. Decisão

Para a **Fase 1 (MVP Core de Pedidos)**, a decisão é:

- **Adotar stack não reativa (Spring MVC/Spring Web) com uso de virtual threads**, em vez de Spring WebFlux.
- Utilizar **Java 25 LTS** como base do projeto, em conjunto com **Spring Boot 4.x** como framework principal, assumindo suporte estável dessa linha à versão de Java escolhida.

## 5. Justificativas

- A carga e o perfil de uso previstos (até dezenas de requisições/segundo, sem cenários intensivos de streaming) **não exigem** os benefícios específicos da stack reativa.
- A simplicidade do modelo imperativo facilita:
  - Adoção de TDD.
  - Legibilidade do código.
  - Manutenção e onboarding do time.
- Virtual threads permitem suportar I/O bloqueante (JDBC, HTTP) com boa escalabilidade, reduzindo a pressão por uma migração imediata a WebFlux.
- Mantém-se um caminho de migração futura: se, em fases posteriores, surgirem requisitos fortes de streaming ou carga extrema, pode-se introduzir serviços específicos usando WebFlux sem reescrever todo o backend.

## 6. Consequências

- **Positivas**
  - Código mais simples e direto, com curva de aprendizado menor.
  - Stack alinhada às ferramentas mais consolidadas do ecossistema Spring.
  - Facilidade de integrar bibliotecas que ainda não oferecem drivers reativos.

- **Negativas / trade-offs**
  - Menor aproveitamento das vantagens máximas de I/O não bloqueante em cenários extremos.
  - Dependência de versões de Java/Spring que suportem bem virtual threads (Java 21+, Spring Boot 3.x/4.x com configurações adequadas).

Além disso, foram adotadas as seguintes diretrizes de código:

- Uso de **records** em Java para DTOs e tipos de dados imutáveis onde fizer sentido.
- Não utilização de **Lombok**, evitando dependência de geração de código por anotações externas e privilegiando recursos nativos da linguagem.
- Uso de **@ControllerAdvice/@RestControllerAdvice** para tratamento centralizado de erros e mapeamento de exceções de domínio para o padrão de resposta de erro definido na API.

## 7. Ações Futuras

- Monitorar o roadmap de **Java 25 LTS** e da linha **Spring Boot** para garantir suporte oficial e boas práticas no uso de virtual threads.
- Revisitar esta decisão se:
  - O volume de requisições crescer substancialmente além do previsto.
  - Surgirem requisitos fortes de streaming em tempo real (por exemplo, rastreamento massivo de entregadores, SSE/WebSocket em grande escala).
 - Avaliar o uso de **Spring AOT/native image** (por exemplo, via GraalVM) para reduzir tempo de inicialização e consumo de memória, caso o custo adicional de build/observabilidade seja aceitável para o contexto do projeto.
 - Avaliar o uso de **CDS/AppCDS** do Java para otimizar tempo de startup em cenários onde isso traga benefício perceptível.
