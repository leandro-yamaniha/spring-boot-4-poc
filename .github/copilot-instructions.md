# Copilot Instructions

Resumo das regras do projeto para o Copilot. Sempre priorizar estas diretrizes ao sugerir código.

## Estilo de Código

- **Sem comentários em código**
  - Não gerar `//`, `/* */` ou `/** */` em Java ou outros arquivos de produção e teste.
  - Prefira nomes claros de classes, métodos e variáveis em vez de comentários.

- **Nomes descritivos e Clean Code**
  - Métodos curtos, com responsabilidade única.
  - Evitar abreviações não óbvias.
  - Usar tipos imutáveis e coleções imutáveis sempre que fizer sentido.

- **Tratamento de erros**
  - Não engolir exceções; sempre propagar ou tratar com mensagem clara.
  - Evitar lançar `RuntimeException` genérica; criar exceções de domínio específicas.

## Arquitetura

- **Use Cases em vez de Services genéricos**
  - Cada caso de uso em uma classe dedicada (`CreateOrderUseCase`, `GetOrderUseCase`, etc.).
  - Injetar dependências via construtor (DIP).

- **Domínio rico**
  - Colocar regras de negócio nas entidades/objetos de domínio (`Pedido`, `ItemDePedido`).
  - Evitar modelos anêmicos apenas com getters/setters.

- **Padrões**
  - Quando houver muitos `if` ou `switch`, considerar Strategy, Factory, State, Chain of Responsibility ou Specification, conforme apropriado.

## Testes

- **TDD sempre que possível**
  - Criar ou ajustar testes antes de implementar mudanças em regras de negócio.
  - Para correções de bug, primeiro reproduzir com um teste que falha.

- **Cobertura de regras críticas**
  - Focar em autenticação/autorização (quando existir), validações, integrações externas e cálculos de valores.

## Qualidade Estática

- **Checkstyle**
  - Respeitar `CustomImportOrder`:
    - Grupo `java.*`, depois `jakarta.*` e demais terceiros (`org.*`, etc.).
    - Por último `com.poc.delivery.*` em grupo separado por linha em branco.
    - Não usar imports com `*`.

- **SonarQube**
  - Evitar variáveis locais não usadas ou atribuições inúteis.
  - Manter complexidade de métodos baixa; sugerir extração de métodos quando necessário.

## Logging e Segurança

- Usar `LogEvent` para logs relevantes de domínio (`ORDER_CREATED`, `ORDER_RETRIEVED`, etc.).
- Não logar dados sensíveis (tokens, senhas, cookies). Quando necessário, mascarar.
- Em filtros HTTP, seguir o padrão existente de redigir headers sensíveis.

## Documentação e Collections

- Atualizar `CHANGELOG.md` em linguagem focada no usuário quando adicionar funcionalidades relevantes.
- Para novos endpoints REST, alinhar com o guia `Criacao-de-Endpoint.md` e, se fizer sentido, adicionar request correspondente nas collections do Bruno.
