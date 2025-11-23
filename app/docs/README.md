# Documentação do Projeto

Este diretório contém documentação externa do código.

## 📋 Quando Documentar Aqui

### ❌ NÃO documente se:

1. **O código já é claro**
   - Nomes descritivos eliminam necessidade
   - Princípios de Clean Code foram seguidos
   - Qualquer desenvolvedor consegue entender lendo o código

2. **Já está documentado em outro lugar**
   - `README.md` do projeto
   - `planejamento/` - decisões arquiteturais e ADRs
   - `historias/` - requisitos e modelagem de features

### ✅ DOCUMENTE se:

1. **Design Pattern como Guia**
   - Padrão usado serve de referência para o time
   - Exemplo de implementação que deve ser seguido
   - Localização: `app/docs/patterns/`

2. **Decisão Técnica Complexa**
   - Algoritmo não trivial que precisa de contexto
   - Trade-offs que não ficam claros no código
   - Localização: `app/docs/decisions/`

3. **Guia de Integração**
   - Como integrar com serviços externos
   - Configurações específicas necessárias
   - Localização: `app/docs/integrations/`

## 📁 Estrutura

```
app/docs/
├── README.md (este arquivo)
├── patterns/          # Design patterns usados como guia
├── decisions/         # Decisões técnicas complexas
└── integrations/      # Guias de integração externa
```

## 🎯 Princípio

**Código deve ser auto-explicativo. Documentação externa é exceção, não regra.**

Antes de documentar, pergunte:
1. Posso melhorar o código para ser mais claro?
2. Isso já está documentado em outro lugar?
3. Isso realmente serve de guia para o time?

Se a resposta para todas for "não", então documente aqui.
