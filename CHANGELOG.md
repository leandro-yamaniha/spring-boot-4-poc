# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Unreleased]

## [0.2.0] - 2025-11-25

### Added

- 🔍 **Consulta de Pedido por ID**
  - Novo endpoint `GET /api/v1/orders/{id}` para buscar um pedido específico.
  - Use case dedicado `GetOrderUseCase` com `OrderNotFoundException` para pedidos inexistentes.
  - Tratamento de erro padronizado: 404 com `error.code = ORDER_NOT_FOUND` e mensagem clara.
  - Testes unitários de use case e controller (MockMvc) cobrindo cenários 200 e 404.

- 🧪 **Melhorias no Fluxo de Qualidade Local**
  - Script `scripts/sonar-local.sh` agora:
    - Sobe o SonarQube em Docker automaticamente.
    - Executa `gradlew clean build` antes da análise.
    - Consulta a API do Sonar e lista issues diretamente no terminal (arquivo, linha, severidade, tipo).
    - Faz o pipeline falhar se houver qualquer issue aberta (Zero Tolerance).

- 📚 **Documentação Aprimorada**
  - Guia `Criacao-de-Endpoint.md` atualizado com exemplo completo do GET por ID.
  - Nova seção de *Troubleshooting* com cenários 400, 404 e 500 e como investigar.

### Changed

- ♻️ **Organização e Padrões de Código**
  - `checkstyle.xml` atualizado para usar `CustomImportOrder` com grupos explícitos de imports.
  - Ajuste da ordem de imports em classes-chave (`OrderController`, `OrderControllerTest`, `HttpRequestLoggingFilter`, `PedidoEntity`, `OpenApiConfig`) para manter consistência e legibilidade.

## [0.1.0] - 2025-11-24

### Added

- 🎉 **Sistema de Criação de Pedidos Completo**
  - **Criar pedidos facilmente**: API intuitiva para registrar pedidos com cliente, loja e itens
  - **Cálculo automático de preços**: Total, frete grátis (pedidos ≥ R$ 100) e descontos (5% para 3+ itens)
  - **Flexibilidade nos pedidos**: Até 10 observações personalizadas por item
  - **Validação inteligente**: Verificação automática de dados obrigatórios e consistência
  - **Respostas claras**: Mensagens de erro compreensíveis quando algo dá errado

- 🚀 **Experiência do Desenvolvedor Melhorada**
  - **Documentação interativa**: Interface Swagger para testar a API diretamente
  - **Ambiente local completo**: Docker configurado para desenvolvimento rápido
  - **Scripts automatizados**: Ferramentas para análise de qualidade e testes
  - **Estrutura organizada**: Código fácil de entender e manter

### Fixed

- 🔧 **Problemas de Qualidade Resolvidos**
  - Correção de pequenos problemas identificados nas análises automáticas

### Removed

- 🧹 **Limpeza de Arquivos**
  - Remoção de arquivos temporários e artefatos de build commitados por engano

## [0.0.1-SNAPSHOT] - 2025-11-23

### Added

- 🏗️ **Projeto Inicializado**
  - Estrutura base do backend Spring Boot 4.0.0
  - Configuração completa para desenvolvimento profissional
