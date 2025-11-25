# Guia de Boas Práticas para Agents e Contribuidores

Este documento define regras mínimas para qualquer alteração automatizada ou manual no repositório, focando em:

- **TDD (Test-Driven Development)**
- **Clean Code**
- **Lint e formatação**
- **Padrões de segurança OWASP**
- **Testes unitários e de integração**
- **Regras mínimas para cada commit/push**
- **Atualização de README e CHANGELOG**

---

## 1. Princípios Gerais

- **Priorizar legibilidade sobre "esperteza"**  
  Código deve ser fácil de entender por qualquer pessoa do time.

- **Evitar duplicação de lógica**  
  Sempre que encontrar comportamento igual sendo repetido, extrair para função/módulo reutilizável.

- **Responsabilidades pequenas e bem definidas**  
  Funções, classes e módulos devem ter um único motivo claro para mudar.

- **Autonomia e segurança**  
  Toda automação (agents, scripts, pipelines) deve respeitar estas regras antes de escrever no repositório.

---

## 2. TDD – Test-Driven Development

- **Ciclo obrigatório para novas regras de negócio**:
  - **Red**: escrever um teste que falha representando o comportamento desejado.
  - **Green**: implementar o mínimo de código para fazer o teste passar.
  - **Refactor**: refatorar o código mantendo todos os testes passando.

- **Novos recursos**  
  Cada nova funcionalidade deve vir acompanhada de testes cobrindo os fluxos principais (felizes e de erro previsível).

- **Correção de bugs**  
  Antes de corrigir um bug, escrever um teste que reproduz o erro. Só então aplicar o fix.

- **Cobertura mínima (qualitativa)**  
  Mais importante do que porcentagem é garantir cobertura de **regras críticas**: autenticação, autorização, validações, integrações externas e cálculos sensíveis.

---

## 3. Clean Code

- **Nomes claros**  
  - Funções e métodos descrevem *o que fazem*.  
  - Variáveis descrevem *o que representam*.  
  - Evitar abreviações não óbvias.

- **Tamanho de funções e classes**  
  - Funções curtas, com um fluxo principal claro.
  - Classes/módulos agrupam comportamentos relacionados.

- **Tratamento de erros explícito**  
  - Não engolir exceções silenciosamente.
  - Propagar erros com contexto útil (mensagens claras) sem vazar informações sensíveis.

- **Código auto-explicativo - SEM comentários**  
  - ❌ **PROIBIDO**: Todos os tipos de comentários (`//`, `/* */`, `/** */`)
  - ✅ **OBRIGATÓRIO**: Nomes descritivos que eliminam necessidade de comentários
  - **Razão**: Código deve ser claro o suficiente para dispensar comentários
  - **Documentação externa**: Use `app/docs/` para guias e padrões
  - **Antes de documentar, pergunte**:
    1. O código está seguindo Clean Code? Nomes claros?
    2. Já está documentado em outro lugar? (README, docs/, planejamento/)
    3. É um design pattern que serve de guia? → Documente em `app/docs/patterns/`
  - Se sentir necessidade de comentar, refatore o código para ser mais claro
  - Comentários mentem com o tempo; código bem escrito não

- **Programação Defensiva - Robustez e Segurança**
  - **Validações de entrada obrigatórias**: Sempre validar parâmetros de métodos públicos
  - **Null checks preventivos**: Evitar NullPointerException com verificações adequadas
  - **Tipos imutáveis**: Preferir records e collections imutáveis para evitar efeitos colaterais
  - **Fail-fast**: Detectar problemas o mais cedo possível, não deixar propagar
  - **Asserções defensivas**: Usar `Objects.requireNonNull()` para contratos claros
  - **Limites de recursos**: Validar tamanhos de listas, strings e uploads
  - **Tratamento de exceções específicas**: Não usar Exception genérica, criar tipos específicos
  - **Logs de auditoria**: Registrar operações críticas para rastreamento

---

## 4. Princípios SOLID e Arquitetura

### SOLID Principles

- **S - Single Responsibility Principle (SRP)**  
  - Cada classe/módulo deve ter uma única responsabilidade.
  - Use Cases devem ter uma única ação (ex: `CreateOrderUseCase`, não `OrderService` genérico).
  - Facilita testes, manutenção e compreensão do código.

- **O - Open/Closed Principle (OCP)**  
  - Aberto para extensão, fechado para modificação.
  - Usar interfaces e abstrações para permitir novos comportamentos sem alterar código existente.

- **L - Liskov Substitution Principle (LSP)**  
  - Subtipos devem ser substituíveis por seus tipos base.
  - Implementações de interfaces devem respeitar o contrato esperado.

- **I - Interface Segregation Principle (ISP)**  
  - Interfaces específicas são melhores que interfaces genéricas.
  - Não forçar clientes a depender de métodos que não usam.

- **D - Dependency Inversion Principle (DIP)**  
  - Depender de abstrações, não de implementações concretas.
  - Usar injeção de dependências via construtor.

### Arquitetura: Use Cases vs Services

**Usar Use Cases em vez de Services tradicionais:**

- **Use Case** = Uma ação específica do usuário
  - Exemplo: `CreateOrderUseCase`, `CancelOrderUseCase`, `GetOrderByIdUseCase`
  - Método principal: `execute(request)` ou `execute(id)`
  - Responsabilidade única e bem definida

- **Evitar Services genéricos** que acumulam múltiplas responsabilidades
  - ❌ `OrderService` com 20 métodos diferentes
  - ✅ Múltiplos use cases, cada um com sua responsabilidade

**Estrutura de um Use Case:**

```java
@Component
public class CreateOrderUseCase {
    private final OrderValidator validator;
    private final PriceCalculator calculator;
    private final OrderRepository repository;
    
    // Injeção via construtor (DIP)
    public CreateOrderUseCase(
        OrderValidator validator,
        PriceCalculator calculator,
        OrderRepository repository
    ) {
        this.validator = validator;
        this.calculator = calculator;
        this.repository = repository;
    }
    
    // Método execute - ponto de entrada único (SRP)
    public PedidoResponse execute(PedidoRequest request) {
        validator.validate(request);
        BigDecimal total = calculator.calculate(request);
        Pedido pedido = Pedido.create(request, total);
        Pedido saved = repository.save(pedido);
        return PedidoResponse.from(saved);
    }
}
```

**Benefícios:**

- ✅ Testabilidade: cada use case testado independentemente
- ✅ Manutenibilidade: mudanças isoladas, sem efeitos colaterais
- ✅ Legibilidade: código auto-documentado
- ✅ Escalabilidade: fácil adicionar novos use cases
- ✅ Alinhamento com Clean Architecture e DDD

**Referências:**

- Ver `planejamento/analise-arquiteturas-backend.md` para detalhes da decisão arquitetural
- Ver `historias/fase1/HIST-001-modelagem.md` para exemplos práticos

---

## 4.1. Boas Práticas de Design

### Quando Usar Cada Tipo de Classe

**DTOs (Data Transfer Objects):**

- Transferência de dados entre camadas (API ↔ Use Cases)
- Exemplo: `PedidoRequest`, `PedidoResponse`
- Apenas dados, sem lógica de negócio
- Validações básicas (@NotNull, @Size, etc)

**Uso de record vs class:**

- `record` para DTOs de entrada/saída e projeções de leitura sem comportamento
- `class` para:
  - Entidades e agregados de domínio ricos (com comportamento e invariantes)
  - Value Objects com validações mais complexas
  - Entidades JPA/anotações de infraestrutura (requer construtor padrão, setters, proxies)

**Value Objects (VOs):**

- Representam conceitos de domínio imutáveis
- Exemplo: `Email`, `CPF`, `Money`, `Address`
- Contêm validações e comportamentos relacionados ao conceito
- Igualdade baseada em valor, não em identidade

**Entities:**

- Representam conceitos com identidade única
- Exemplo: `Pedido`, `Cliente`, `Produto`
- Contêm lógica de negócio relevante
- Igualdade baseada em ID

**Helpers/Utils:**

- ⚠️ **Usar com moderação** - podem indicar falta de coesão
- Apenas para funções verdadeiramente utilitárias e genéricas
- Exemplo: `DateUtils.formatBrazilianDate()`, `StringUtils.removeAccents()`
- ❌ Evitar: `PedidoHelper` com lógica de negócio → mover para o domínio

**Mappers:**

- Conversão entre camadas (Entity ↔ DTO, Domain ↔ Entity)
- Exemplo: `PedidoMapper.toResponse(Pedido)`
- Sem lógica de negócio, apenas transformação de estrutura
- Preferir **MapStruct** para mapeamentos estruturais sem regra de negócio
- componentModel padrão: `spring`
- Se a conversão envolver regra de negócio, manter essa lógica no domínio/use case, não no mapper

### Evitar IFs Excessivos - Design Patterns

**Quando encontrar múltiplos IFs, considerar:**

**1. Strategy Pattern**

```java
// ❌ Evitar
if (tipoPagamento.equals("CREDITO")) {
    processarCredito();
} else if (tipoPagamento.equals("DEBITO")) {
    processarDebito();
} else if (tipoPagamento.equals("PIX")) {
    processarPix();
}

// ✅ Usar Strategy
interface PaymentStrategy {
    void process(Payment payment);
}

class CreditCardStrategy implements PaymentStrategy { ... }
class DebitCardStrategy implements PaymentStrategy { ... }
class PixStrategy implements PaymentStrategy { ... }
```

**2. Polymorphism (OOP básico)**

```java
// ❌ Evitar
if (pedido.getStatus() == Status.CRIADO) {
    // lógica para criado
} else if (pedido.getStatus() == Status.CONFIRMADO) {
    // lógica para confirmado
}

// ✅ Usar Polimorfismo
abstract class PedidoState {
    abstract void process(Pedido pedido);
}

class CriadoState extends PedidoState { ... }
class ConfirmadoState extends PedidoState { ... }
```

**3. Factory Pattern**

```java
// ❌ Evitar
if (tipo.equals("LOJA")) {
    return new LojaValidator();
} else if (tipo.equals("CLIENTE")) {
    return new ClienteValidator();
}

// ✅ Usar Factory
class ValidatorFactory {
    public Validator create(String tipo) {
        return switch(tipo) {
            case "LOJA" -> new LojaValidator();
            case "CLIENTE" -> new ClienteValidator();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

**4. Chain of Responsibility**

```java
// Para validações sequenciais
class ValidationChain {
    private Validator next;
    
    public void validate(Request request) {
        // valida
        if (next != null) next.validate(request);
    }
}
```

**5. Specification Pattern**

```java
// Para regras de negócio complexas
interface Specification<T> {
    boolean isSatisfiedBy(T entity);
}

class PedidoPodeSerCanceladoSpec implements Specification<Pedido> {
    public boolean isSatisfiedBy(Pedido pedido) {
        return pedido.getStatus() == Status.CRIADO 
            && pedido.getCreatedAt().isAfter(now().minusHours(1));
    }
}
```

### Princípios Gerais

**Tell, Don't Ask:**

```java
// ❌ Evitar (perguntando)
if (pedido.getStatus() == Status.CRIADO) {
    pedido.setStatus(Status.CONFIRMADO);
}

// ✅ Usar (dizendo)
pedido.confirmar();
```

**Evitar Anemia de Domínio:**

```java
// ❌ Modelo anêmico
class Pedido {
    private BigDecimal total;
    // apenas getters/setters
}

// ✅ Modelo rico
class Pedido {
    private BigDecimal total;
    
    public void adicionarItem(Item item) {
        validarItem(item);
        itens.add(item);
        recalcularTotal();
    }
}
```

**Composição sobre Herança:**

- Preferir composição quando possível
- Herança apenas quando há relação "é um" clara
- Evitar hierarquias profundas (> 2-3 níveis)

---

## 5. Lint, Formatação e Qualidade Estática

- **Linter obrigatório antes de cada commit**  
  Sempre executar o(s) linter(s) configurado(s) no projeto (frontend, backend(s), scripts) antes de commitar.

- **Formatação automática**  
  Utilizar o formatador configurado (por exemplo, Prettier, ESLint + autofix, Spotless, etc.) quando disponível.

- **Build sem warnings graves**  
  Não ignorar avisos de compilador/linters que apontem possíveis bugs, problemas de segurança ou APIs obsoletas.

- **CI como guardião**  
  Pipelines de CI devem falhar se lint ou build falharem. Agents **não devem** tentar contornar essas falhas.

---

## 6. Segurança – Diretrizes OWASP

Toda contribuição deve respeitar, no mínimo, os princípios básicos do **OWASP Top 10**. Entre eles:

- **Validação e sanitização de entrada**  
  Nunca confiar em dados vindos do cliente ou de fontes externas. Validar tipos, tamanhos, formatos e valores permitidos.

- **Autenticação e autorização robustas**  
  - Proteger recursos sensíveis com autenticação.  
  - Verificar se o usuário tem permissão para acessar/modificar cada recurso.

- **Proteção contra injeção**  
  Utilizar APIs preparadas / parâmetros vinculados em vez de concatenar strings para consultas ou comandos externos.

- **Proteção de dados sensíveis**  
  - Nunca logar senhas, tokens, dados de cartão ou informações pessoais sensíveis.  
  - Utilizar armazenamento seguro para segredos (variáveis de ambiente, vault, etc.).

- **Manuseio seguro de erros**  
  - Mensagens para o usuário não devem revelar detalhes internos de stack, queries ou estrutura interna.  
  - Logs internos podem conter mais detalhes, mas sem expor dados sigilosos.

- **Dependências seguras**  
  - Manter bibliotecas e frameworks atualizados.  
  - Evitar dependências obsoletas ou sem manutenção conhecida.

---

## 7. Testes Unitários

- **Obrigatórios para lógica de negócio**  
  Toda função/método com regra de negócio relevante deve ter testes unitários cobrindo:
  - Fluxos de sucesso (happy path).
  - Fluxos de falha esperada (entradas inválidas, exceções de domínio).

- **Testes determinísticos**  
  - Não depender de horário real, rede externa ou ordem não determinística.  
  - Utilizar mocks/stubs/fakes quando necessário.

- **Critério de aceitação para merge**  
  Nenhum commit que altera código de produção deve ser mesclado sem testes unitários adequados.

---

## 8. Testes de Integração

- **Cobertura de fluxos entre componentes**  
  Escrever testes de integração para:
  - Comunicação entre módulos internos.  
  - Acesso a bancos de dados, filas, serviços externos (com ambientes de teste ou mocks realistas).  
  - Endpoints HTTP principais (controllers/APIs) e seus fluxos end-to-end no backend.

- **Ambiente isolado**  
  - Utilizar bancos de teste, containers ou perfis específicos de teste.  
  - Nunca apontar testes automatizados para ambientes de produção.

- **Reprodutibilidade**  
  - Testes devem poder ser executados localmente com instruções claras.  
  - Dados de teste devem ser criados e limpos pelo próprio teste.

---

## 9. Regras Mínimas para Cada Commit

Qualquer commit (manual ou criado por agents) **deve obedecer a todos os itens abaixo** **antes de ser criado**:

**⚠️ CRÍTICO: Executar `./gradlew clean build` (ou comando equivalente) e garantir sucesso ANTES de criar o commit.**

**🔍 OBRIGATÓRIO: Executar `./scripts/sonar-local.sh` para verificar compliance de qualidade ANTES de criar o commit.**

1. **Build completo com sucesso**
   - Executar build completo do projeto (`./gradlew clean build` ou equivalente).
   - Build deve completar sem erros (exit code 0).
   - Todos os módulos impactados devem compilar com sucesso.  
   - Aplicação (ou serviços afetados) devem iniciar sem erros em ambiente local ou de teste configurado.
   - **Nunca commitar código que não builda com sucesso.**

2. **Análise de qualidade SonarQube (OBRIGATÓRIO)**
   - Executar `./scripts/sonar-local.sh` antes de cada commit.
   - Quality Gate "Zero Tolerance" deve passar:
     - Cobertura de testes: 100%
     - Bugs: 0
     - Code Smells: 0
   - Se a análise falhar, corrigir os problemas antes de commitar.
   - **Nunca commitar código que não passe no Quality Gate.**

3. **Testes passando**
   - Testes unitários relevantes executam com sucesso.  
   - Testes de integração impactados pela mudança também passam.  
   - Não adicionar commits que quebrem o pipeline de CI.

4. **Lint sem erros**
   - Nenhum erro de linter pendente.  
   - Warnings relevantes avaliados e, se possível, eliminados.

5. **Escopo pequeno e coeso**
   - Cada commit deve focar em uma mudança lógica (feature, bugfix, refactor) bem definida.  
   - Evitar commits misturando refactor grande com mudança funcional.

6. **Mensagem de commit clara**
   - Descrever o que foi feito e, se relevante, o porquê.  
   - Se houver padrão de mensagem (ex.: Conventional Commits), segui-lo.

7. **CHANGELOG atualizado**
   - **Obrigatório**: Atualizar o CHANGELOG.md a cada commit que adiciona, modifica ou remove funcionalidades.
   - Adicionar entrada com data, tipo da mudança (Added/Changed/Fixed/Removed) e descrição concisa.
   - Manter formato consistente (sugestão: [Keep a Changelog](https://keepachangelog.com/)).
   - Commits de refactor interno sem impacto funcional podem omitir entrada no CHANGELOG.

8. **Versionamento Semântico**
   - Seguir [Semantic Versioning 2.0.0](https://semver.org/lang/pt-BR/) (MAJOR.MINOR.PATCH).
   - Atualizar versão conforme o tipo de commit (Conventional Commits):
     - **feat:** → incrementa MINOR (0.1.0 → 0.2.0)
     - **fix:** → incrementa PATCH (0.1.0 → 0.1.1)
     - **BREAKING CHANGE:** → incrementa MAJOR (0.1.0 → 1.0.0)
     - **chore:, docs:, style:, refactor:, test:** → não alteram versão
   - Atualizar versão em:
     - `build.gradle` (ou equivalente)
     - `CHANGELOG.md` (criar nova seção de versão ao fazer release)
   - Versões de desenvolvimento usam sufixo `-SNAPSHOT`

9. **Verificação e atualização do README.md**
   - **Obrigatório**: Antes de cada commit, verificar o que está sendo commitado.
   - Avaliar se as mudanças impactam a documentação do usuário:
     - Novos recursos, endpoints, comandos ou funcionalidades
     - Mudanças em configuração, instalação ou uso
     - Novos scripts, ferramentas ou dependências
     - Alterações em comportamento existente
     - Novos requisitos ou pré-requisitos
   - Se houver impacto, **atualizar o README.md** antes do commit:
     - Adicionar instruções de uso para novos recursos
     - Atualizar comandos ou exemplos
     - Documentar novas configurações
     - Atualizar seções de instalação/setup se necessário
   - O README deve sempre refletir o estado atual do código
   - **Nunca commitar código sem documentação adequada**

---

## 11. Regras para Push: README, CHANGELOG e Versionamento

Para **cada push** que altera comportamento observável da aplicação (features, endpoints, fluxos, contratos, performance relevante):

1. **Atualizar README**

   - Documentar novos recursos importantes.  
   - Atualizar seções de instalação, execução, endpoints, ambientes ou pré-requisitos, se forem impactados.

2. **Atualizar Versão**

   - Seguir Versionamento Semântico conforme descrito na seção 8:
     - **feat:** → incrementa MINOR (0.1.0 → 0.2.0)
     - **fix:** → incrementa PATCH (0.1.0 → 0.1.1)
     - **BREAKING CHANGE:** → incrementa MAJOR (0.1.0 → 1.0.0)
     - **chore:, docs:, style:, refactor:, test:** → não alteram versão
   - Atualizar versão em:
     - `build.gradle` (ou equivalente)
     - `CHANGELOG.md` (criar nova seção de versão ao fazer release)
   - Versões de desenvolvimento usam sufixo `-SNAPSHOT`

3. **Atualizar CHANGELOG**

   - Adicionar entrada descrevendo as mudanças mais relevantes desde a última versão/entrada.  
   - Seguir um formato consistente (por exemplo: data, tipo da mudança, breve descrição).
   - **Focar no valor para o usuário**: Descrever o que o usuário ganha, não detalhes técnicos.
   - **Usar linguagem acessível**: Evitar jargões técnicos excessivos.
   - **Destacar funcionalidades**: O que o usuário pode fazer agora que não podia antes.
   - **Manter seções padrão**: Added, Changed, Fixed, Removed (seguir Keep a Changelog).

### 10.1. Guia de Escrita de CHANGELOG Focado no Usuário

**Princípios fundamentais para escrever um CHANGELOG que importa:**

#### 🎯 **Foco no Valor Entregue**
- **O que mudou para o usuário?** Não "implementei API REST", mas "agora você pode criar pedidos facilmente"
- **Benefícios claros**: "Cálculo automático de preços", "Respostas claras de erro"
- **Funcionalidades destacadas**: O que o usuário ganha de novo

#### 📝 **Estrutura Recomendada por Versão**

```markdown
## [0.1.0] - 2025-11-24

### Added
- 🎉 **Sistema de Criação de Pedidos Completo**
  - **Criar pedidos facilmente**: API intuitiva para registrar pedidos
  - **Cálculo automático**: Total + frete grátis + descontos
  - **Validação inteligente**: Dados verificados automaticamente

- 🚀 **Melhoria na Experiência do Desenvolvedor**
  - **Documentação interativa**: Interface Swagger para testar
  - **Ambiente local**: Docker configurado para desenvolvimento

### Fixed
- 🔧 **Problemas de qualidade resolvidos**: Correções automáticas aplicadas

### Removed
- 🧹 **Limpeza**: Arquivos temporários removidos
```

#### ✅ **Boas Práticas**
- **Evitar detalhes técnicos**: Não mencionar "arquitetura Clean", "SOLID", "testes unitários"
- **Usar emojis**: Para tornar visualmente atraente e organizado
- **Ser conciso**: Descrições diretas e impactantes
- **Pensar no usuário final**: O que ele ganha, não como foi implementado
- **Seguir padrão Keep a Changelog**: Added, Changed, Fixed, Removed

#### ❌ **Evitar**
- Detalhes de implementação ("Controller → Use Case → Repository")
- Jargões técnicos ("SOLID principles", "Domain-Driven Design")
- Nomes de classes/componentes ("CreateOrderUseCase", "PedidoEntity")
- Métricas técnicas ("100% cobertura", "Quality Gate aprovado")
- Informações irrelevantes para o usuário ("Stack Tecnológica", "Endpoints")

#### 📊 **Exemplo Real da HIST-001**

**Antes (técnico):**
```
- API REST com endpoint POST /api/v1/orders
- Modelo de domínio rico (Pedido, ItemDePedido, StatusPedido)
- Arquitetura Clean Architecture (Controller → Use Case → Repository)
```

**Depois (user-centric):**
```
🎉 Sistema de Criação de Pedidos Completo
  ✓ Criar pedidos facilmente: API intuitiva para registrar pedidos
  ✓ Cálculo automático de preços: Total, frete grátis, descontos
  ✓ Validação inteligente: Verificação automática de dados
```

**Resultado**: CHANGELOG que comunica valor real para stakeholders!

4. **Verificar conclusão de histórias**

   - **Obrigatório**: A cada push, verificar se alguma fase/história foi completada
   - Revisar documentação em [`historias/`](historias/):
     - Marcar histórias como concluídas se todos os critérios de aceitação foram atendidos
     - Atualizar status das features (em andamento, concluída, bloqueada)
     - Documentar decisões técnicas tomadas durante a implementação
   - Atualizar documentação de planejamento se necessário:
     - [`planejamento/documento-executivo-backend-entregas.md`](planejamento/documento-executivo-backend-entregas.md)
     - Roadmap e marcos do projeto
   - Garantir rastreabilidade entre código e histórias

5. **Sincronia com código**

   - O que está no README e no CHANGELOG deve refletir o estado atual do repositório.  
   - Não deixar documentação desatualizada após merges significativos.

---

## 12. Regras para Branches e Histórias

**Toda implementação deve seguir o fluxo de branches vinculadas às histórias:**

1. **Criar branch a partir da história**

   - Nomenclatura: `feature/HIST-XXX-descricao-curta` ou `fix/HIST-XXX-descricao-curta`
   - Exemplos:
     - `feature/HIST-001-criacao-pedido`
     - `fix/HIST-002-validacao-endereco`
     - `feature/HIST-003-integracao-pagamento`
   - Branch deve referenciar a história em [`historias/`](historias/)

2. **Desenvolvimento na branch**

   - Commits devem referenciar a história: `feat(HIST-001): adicionar endpoint de criação de pedido`
   - Seguir todas as regras de commit da seção 8
   - Manter branch atualizada com `main`

3. **Pull Request**

   - Título deve incluir referência à história: `[HIST-001] Implementar criação de pedido`
   - Descrição deve incluir:
     - Link para a história
     - Critérios de aceitação atendidos
     - Testes implementados
     - Screenshots/evidências (se aplicável)
   - Passar por code review
   - Passar em todos os quality gates

4. **Merge para main**

   - Apenas após aprovação do PR
   - Squash commits se necessário para manter histórico limpo
   - Atualizar status da história como concluída
   - Deletar branch após merge

### 12.1. Configuração Local Sempre Atualizada

**Toda história DEVE manter a configuração para execução local sempre atualizada:**

#### **Na Fase de Testes**

- ✅ **Atualizar collections**: Sempre que a API mudar, atualizar Bruno/Postman collections
- ✅ **Scripts automatizados**: Garantir que scripts de execução local funcionem
- ✅ **Ambiente Docker**: Verificar se containers estão configurados corretamente
- ✅ **Documentação de execução**: Instruções em `infra/local/README.md` atualizadas

#### **Antes de Gerar PR**

- ✅ **Último commit dedicado**: Criar commit específico com updates finais
- ✅ **Versionamento**: Atualizar versão no `build.gradle` conforme regras semânticas
- ✅ **CHANGELOG**: Documentar mudanças seguindo guia da seção 10.1
- ✅ **README**: Atualizar se houver impactos na documentação do usuário
- ✅ **Collections**: Atualizar com novos endpoints/testes
- ✅ **Quality Gate**: Garantir que `./scripts/sonar-local.sh` passe
- ✅ **Build completo**: `./gradlew clean build` com sucesso

**Formato do último commit antes do PR:**
```
chore: finalizar HIST-XXX - versionar e atualizar docs

- Atualizar versão para X.Y.Z conforme semantic versioning
- CHANGELOG.md: documentar mudanças seguindo padrão user-centric
- README.md: atualizar documentação se necessário
- infra/local/README.md: atualizar instruções de execução
- collections: atualizar endpoints e testes
- Quality Gate: garantir 100% cobertura e 0 issues
```

#### **Benefícios da Configuração Sempre Atualizada**
- ✅ **Ambiente local funcional**: Qualquer pessoa pode executar imediatamente
- ✅ **Testes facilitados**: Collections atualizadas permitem testes rápidos
- ✅ **Documentação viva**: Sempre reflete o estado atual do código
- ✅ **Deploy confiável**: Ambiente local = ambiente de produção
- ✅ **Onboarding rápido**: Novos devs conseguem executar rapidamente

**Benefícios:**
- ✅ Rastreabilidade completa entre código e requisitos
- ✅ Histórico organizado e navegável
- ✅ Facilita code review e auditoria
- ✅ Permite trabalho paralelo em múltiplas histórias
- ✅ **Configuração local sempre funcional**: Ambiente atualizado automaticamente

---

## 13. Regras Específicas para Agents Automatizados

Agents (como este) **devem SEMPRE**:

- **Respeitar todas as regras acima** antes de sugerir ou aplicar mudanças.
- **Evitar alterações grandes em lote** sem quebrar em passos menores e bem descritos.
- **Não criar arquivos desnecessários**; apenas o que for explicitamente solicitado ou justificado.
- **Descrever claramente** no sumário do que foi feito:
  - Arquivos alterados/criados/removidos.  
  - Razão principal da mudança.  
  - Impacto esperado (build, testes, performance, segurança).

Quando não for possível seguir alguma regra (por limitação de ambiente, tempo ou contexto), o agent deve:

- Explicar claramente qual regra não foi seguida e por quê.
- Sugerir próximos passos para que o time humano regularize a situação (ex.: criação de testes adicionais, ajuste de documentação, correção de warnings, etc.).

---

Este `agents.md` deve ser lido e seguido por qualquer pessoa ou automação que contribua com este repositório.
