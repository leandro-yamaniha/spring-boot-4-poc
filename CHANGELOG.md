# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Unreleased]

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

- 📊 **Qualidade e Confiabilidade**
  - **Sistema robusto**: Cobertura completa de testes garantindo estabilidade
  - **Performance otimizada**: Análises automáticas de qualidade de código
  - **Confiabilidade**: Validação rigorosa antes de cada mudança

### Enhanced

- 📚 **Documentação Completa**
  - **README atualizado**: Guia claro para instalar e usar o sistema
  - **Exemplos práticos**: Payloads de exemplo para criar pedidos
  - **Ambiente de desenvolvimento**: Instruções para configurar tudo localmente

- 🛠️ **Ferramentas de Desenvolvimento**
  - **Análise automática**: Scripts para verificar qualidade do código
  - **Testes automatizados**: Ambiente configurado para testes consistentes
  - **Padronização**: Regras claras para manter qualidade no código

### Changed

- 📋 **Processos de Desenvolvimento**
  - **Workflow otimizado**: Processo mais eficiente para novas funcionalidades
  - **Qualidade garantida**: Verificações automáticas em cada mudança
  - **Documentação viva**: Sempre atualizada com as últimas mudanças

### Issues Criadas para Melhorias Futuras

- **[HIST-002]** - Expandir status do pedido (preparação, entrega, etc.)
- **[HIST-003]** - Validar existência de clientes e produtos antes de criar pedido
- **[HIST-004]** - Controle de estoque em tempo real
- **[HIST-005]** - Métricas e monitoramento da API
- **[HIST-006]** - Segurança e controle de acesso
- **[HIST-007]** - Cache para melhor performance
- **[HIST-008]** - Documentação técnica avançada
- **[HIST-009]** - Suporte a múltiplos idiomas

### Fixed

- 🔧 **Problemas de Qualidade Resolvidos**
  - Correção de pequenos problemas identificados nas análises automáticas

### Stack Tecnológica

- Java 25 (LTS)
- Spring Boot 4.0.0
- Gradle 9.2.1
- Tomcat 11 (embedded)
- SpringDoc OpenAPI 3.0.0
- MapStruct 1.5.5
- Testcontainers 1.21.3
- Cucumber 7.14.0

### Endpoints Disponíveis

- `http://localhost:8080/swagger-ui.html` - Documentação Swagger UI
- `http://localhost:8080/v3/api-docs` - Especificação OpenAPI JSON
- `http://localhost:8080/actuator/*` - Endpoints do Spring Boot Actuator

## [0.0.1-SNAPSHOT] - 2025-11-23

### Inicial

- Inicialização do projeto (HIST-000)
