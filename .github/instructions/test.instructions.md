---
applyTo: "app/src/test/java/**/*.java"
---

# Test Instructions

Diretrizes para o Copilot ao sugerir testes neste repositório.

## Princípios Gerais

- Priorizar **TDD**:
  - Sempre que possível, sugerir primeiro o teste que falha antes da implementação.
  - Para bugs, criar um teste que reproduza o erro antes do fix.
- Testes devem ser **claros e focados** em uma regra de negócio.
- Usar **nomes descritivos** de métodos de teste (em português) que indiquem o cenário e o resultado esperado.

## Ferramentas e Stack

- **JUnit 5** (`@Test`, `@BeforeEach`, tags quando necessário).
- **Mockito** para mocks e stubs.
- **Instancio** para geração de dados em testes complexos.
- **Spring Boot Test + Testcontainers** para testes de integração (já configurados em `DeliveryApplicationTests` e `OrderControllerIT`).

## Estrutura dos Testes

- Testes unitários devem isolar o use case ou componente sob teste:
  - Mockar dependências externas (repositórios, mappers, serviços externos).
  - Verificar interações essenciais com `Mockito.verify(...)` quando fizer sentido.
- Testes de integração devem:
  - Subir o contexto Spring Boot.
  - Usar Testcontainers para banco de dados.
  - Exercitar a API via `MockMvc` ou `RestAssured`, conforme o padrão existente.

## Padrões de Nomenclatura

- Classes de teste terminam com `Test` (ex.: `CreateOrderUseCaseTest`, `OrderControllerTest`).
- Métodos de teste seguem padrão descritivo, por exemplo:
  - `deveCriarPedidoComSucesso()`
  - `deveLancarExcecaoQuandoPedidoNaoEncontrado()`
- Evitar comentários dentro dos testes; o nome do método deve explicar o cenário.

## Cobertura e Qualidade

- Focar em cobrir:
  - Regras de negócio em use cases.
  - Validações de entrada (DTOs, validators).
  - Integrações com repositórios (via mocks ou integração).
- Evitar testes frágeis:
  - Não acoplar a detalhes de implementação que podem mudar facilmente.
  - Verificar apenas o que é relevante para o comportamento.

## Boas Práticas Específicas

- **Use Cases**:
  - Testar fluxos felizes e fluxos de erro previsíveis (ex.: not found, validação, limites).
  - Garantir que exceções de domínio corretas são lançadas com mensagens significativas.
- **Controllers**:
  - Usar `MockMvc` para validar status HTTP, payload e contrato de erro (`error.code`, `error.message`).
  - Incluir `GlobalExceptionHandler` nos testes de controller.
- **Entidades de domínio**:
  - Testar comportamento (métodos que alteram estado), não apenas getters/setters.
- **Filtros / Infra**:
  - Em filtros HTTP, testar logging e mascaramento de dados sensíveis sem depender de logs reais.

## Restrições

- Não gerar comentários (`//`, `/* */`, `/** */`) dentro dos testes.
- Não sugerir código que quebre Checkstyle, Sonar ou as regras de import definidas.
- Não criar testes vazios ou gerados automaticamente sem asserções significativas.
