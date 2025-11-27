# JUnit 6 – Visão Geral, Vantagens e Migração a partir do JUnit 5

## 1. Introdução

O JUnit 6 é a evolução natural do JUnit 5, mantendo o modelo de plataforma (Platform, Jupiter e Vintage), mas com foco em:

- **Limpeza de APIs antigas** e remoção de comportamentos legados.
- **Modernização da base de execução** (Java 17+, Kotlin 2.2+).
- **Unificação de versões** entre os módulos principais.
- **Melhor suporte a nullability**, paralelismo e ferramentas modernas.

O objetivo desta página é servir como guia rápido para o time entender **por que** considerar o JUnit 6 e **como** migrar gradualmente a suíte atual baseada em JUnit 5.

---

## 2. Objetivos do JUnit 6

- **Modernizar o baseline da plataforma**
  - Java 17 como versão mínima suportada.
  - Kotlin 2.2 como versão mínima suportada (para cenários com Kotlin).

- **Unificar a versão dos módulos principais**
  - Plataforma, Jupiter e Vintage passam a compartilhar o **mesmo número de versão** (ex.: `6.0.x`).
  - Facilita alinhamento de dependências e troubleshooting.

- **Melhorar a capacidade de evolução da API**
  - Uso sistemático de anotações de nullability via [JSpecify](https://jspecify.dev/).
  - Foco em remover APIs obsoletas e comportamentos ambíguos herdados de versões anteriores.

- **Aprimorar a experiência de execução de testes**
  - Integração de funcionalidades de JFR (Java Flight Recorder) no `junit-platform-launcher`.
  - Novos recursos de controle de execução (ex.: *fail-fast* e `CancellationToken`).

---

## 3. Vantagens do JUnit 6 em relação ao JUnit 5

- **Base de linguagem atualizada**
  - Garante que os testes rodam em ambientes compatíveis com as versões modernas de Java e Kotlin.
  - Evita problemas de compatibilidade com bibliotecas que já assumem Java 17+.

- **Menos "lixo" histórico na API**
  - Remoção de diversas APIs marcadas como deprecated na série 5.x.
  - Comportamentos ambíguos ou legados são simplificados ou removidos.

- **Modelo de versão mais simples**
  - Uma única versão para Platform, Jupiter e Vintage reduz o risco de misturar combinações incompatíveis.

- **Nullability melhor documentada**
  - Anotações JSpecify ajudam a entender melhor o contrato das APIs (o que pode ser nulo, o que não pode).
  - Facilita uso em IDEs e ferramentas de análise estática.

- **Melhorias pontuais na execução de testes**
  - Ordem determinística para classes anotadas com `@Nested`.
  - Novos orderers padrão (`MethodOrderer.Default`, `ClassOrderer.Default`).
  - Suporte aprimorado a CSV em testes parametrizados.
  - Melhor suporte a `suspend` functions em Kotlin como métodos de teste.

---

## 4. Principais novidades técnicas

### 4.1 Baseline de plataforma

- **Java 17 mínimo**
  - A suíte de testes passa a assumir recursos e semântica do Java 17.
  - Enums de JRE anteriores (JAVA_8 ... JAVA_16) são descontinuados em anotações condicionais.

- **Kotlin 2.2 mínimo (onde aplicável)**
  - Alinha o suporte oficial do JUnit com versões modernas de Kotlin.

- **Unificação de versões entre módulos**
  - Platform, Jupiter e Vintage usam o mesmo número de versão (ex.: `6.0.1`).
  - Facilita configuração via BOMs e reduz divergência entre módulos.

- **Integração com JFR**
  - Funcionalidades que antes dependiam de módulo separado (`junit-platform-jfr`) foram integradas ao `junit-platform-launcher`.

### 4.2 JSpecify e nullability

- Todos os módulos do JUnit 6 passam a utilizar anotações de nullability do [JSpecify](https://jspecify.dev/).
- Benefícios práticos:
  - Melhora a documentação do contrato das APIs.
  - Ajuda ferramentas de análise estática a encontrar problemas de null mais cedo.

### 4.3 Melhorias em Jupiter e ordenação

- **Ordem determinística de `@Nested`**
  - As classes internas anotadas com `@Nested` agora são executadas em ordem determinística (ainda que não óbvia).
  - Reduz a chance de flakiness causada por dependência acidental de ordem.

- **Orderers padrão para métodos e classes**
  - `MethodOrderer.Default` e `ClassOrderer.Default` fornecem política padrão consistente de ordenação.
  - `@TestMethodOrder` passa a ser herdado por classes `@Nested` anexas, simplificando a configuração.

### 4.4 CSV e testes parametrizados

- Migração do parser CSV interno para [FastCSV](https://fastcsv.org/):
  - Melhor tratamento de headers em `@CsvSource` e `@CsvFileSource`.
  - Detecção automática de quebras de linha (`\r`, `\n`, `\r\n`).
  - Proibição de certos formatos de CSV malformados, evitando que entradas ambíguas passem silenciosamente.

- Alguns atributos dessas anotações passam a se aplicar também a headers (por exemplo, `ignoreLeadingAndTrailingWhitespace`, `nullValues`).

### 4.5 Execução e controle de fluxo

- **Modo *fail-fast* no `ConsoleLauncher`**
  - Novo parâmetro `--fail-fast` permite abortar a execução de testes ao primeiro erro.

- **`CancellationToken` para cancelar execução**
  - Novo mecanismo para sinalizar cancelamento de execução de testes.
  - Útil em integrações com ferramentas que precisam interromper a suíte em tempo de execução.

---

## 5. Ganhos práticos para o projeto

- **Alinhamento com stack moderna**
  - Se o projeto já usa Java 17+ em produção, alinhar também os testes reduz discrepâncias entre ambientes.

- **Menos risco de APIs obsoletas**
  - Ao migrar para o JUnit 6, o time é forçado a remover usos de construções antigas que já estavam deprecated no JUnit 5.

- **Manutenibilidade a longo prazo**
  - O JUnit 6 é o alvo principal de evolução da plataforma de testes.
  - Corrigir warnings de depreciação agora evita dor de cabeça em futuras versões.

- **Melhor suporte a integrações**
  - Ferramentas modernas (build, IDE, análise, observabilidade) tendem a focar suporte nas versões mais novas do JUnit.

---

## 6. Migração do JUnit 5 para o JUnit 6

### 6.1 Visão geral da migração

A migração de JUnit 5.x para 6.0.x foi pensada para ser **evolutiva**, não uma reescrita completa.

Em resumo:

- A estrutura conceitual (Platform, Jupiter, Vintage) é preservada.
- A maior parte da API usada em testes de dia a dia continua igual.
- A principal fonte de trabalho são **APIs e comportamentos marcados como deprecated** na série 5.x e efetivamente removidos na 6.x.

Há um guia oficial de upgrade mantido pelo time do JUnit:
 
 - [Upgrading to JUnit 6.0](https://github.com/junit-team/junit-framework/wiki/Upgrading-to-JUnit-6.0)

### 6.2 Pré-requisitos

Antes de tentar subir para o JUnit 6, garantir que:

- O projeto está em **Java 17 ou superior**.
- Se houver código de teste em Kotlin, a versão de Kotlin atende o mínimo exigido (2.2 ou superior, conforme release notes atuais).

### 6.3 Passos recomendados

- **Passo 1 – Atualizar o JDK do projeto**
  - Ajustar o toolchain do build (Maven/Gradle) para usar Java 17+.
  - Garantir que a aplicação e os testes compilam e rodam normalmente com JUnit 5 ainda.

- **Passo 2 – Atualizar as dependências do JUnit**
  - Identificar hoje quais artefatos do JUnit 5 estão em uso (por exemplo, `junit-jupiter`, `junit-jupiter-params`, `junit-platform-launcher`, etc.).
  - Atualizar essas mesmas dependências para a versão `6.0.x` correspondente.
  - Se usar BOM, trocar o import do BOM da família 5.x para 6.x.

- **Passo 3 – Rodar todos os testes e coletar erros de compilação/execução**
  - Corrigir erros de compilação causados por remoção de APIs deprecated.
  - Ajustar configurações de engine, plugins ou extensões que dependiam de comportamentos alterados.

- **Passo 4 – Revisar usos de APIs sensíveis**
  - Verificar anotações condicionais baseadas em `JRE` (por exemplo, `@EnabledOnJre`, `@DisabledOnJre`, `@EnabledForJreRange`, `@DisabledForJreRange`) que referenciem versões anteriores ao Java 17.
  - Revisar testes parametrizados com `@CsvSource` e `@CsvFileSource` que dependam fortemente de detalhes de parsing.

### 6.4 Mudanças e quebras mais comuns

Abaixo alguns pontos que podem aparecer durante a migração (a lista não é exaustiva; consultar sempre os release notes oficiais para o conjunto completo).

- **APIs de ordenação removidas ou alteradas**
  - `MethodOrderer.Alphanumeric` foi removido.
    - Substituir por um dos orderers atuais (`MethodOrderer.MethodName`, `MethodOrderer.OrderAnnotation`, `MethodOrderer.DisplayName`) ou pelo novo `MethodOrderer.Default`.

- **Módulos de migração descontinuados**
  - O módulo `junit-jupiter-migrationsupport` está deprecado e previsto para remoção em versões futuras.
  - Evitar depender dele a longo prazo; preferir testes já nativos em Jupiter.

- **Anotações condicionais com JRE antigos**
  - Constantes `JRE.JAVA_8` até `JRE.JAVA_16` deixam de fazer sentido com baseline Java 17.
  - Atualizar anotações como `@EnabledOnJre`, `@DisabledOnJre`, `@EnabledForJreRange`, `@DisabledForJreRange` para usar apenas versões suportadas (por exemplo, `JAVA_17` em diante).

- **CSV e testes parametrizados**
  - Erros de parsing passam a ser mais estritos em alguns casos (por exemplo, conteúdo extra após fechamento de aspas).
  - O atributo `lineSeparator` em `@CsvFileSource` foi removido; a detecção agora é automática.
  - Alguns atributos (como `ignoreLeadingAndTrailingWhitespace`, `nullValues`) agora também se aplicam aos headers, o que pode mudar levemente o comportamento esperado.

- **Configurações estritas para enums**
  - Valores inválidos em certas propriedades de configuração (por exemplo, relacionadas a paralelismo, timeouts, lifecycle) passam a causar falha de descoberta/execução de testes.
  - É recomendável revisar configurações personalizadas usadas no projeto para garantir que os valores continuam válidos.

### 6.5 Estratégia de migração gradual

- **Começar pela atualização de ambiente (Java/Kotlin)**
  - Mesmo antes de mexer em dependências do JUnit, subir o JDK do projeto para 17+ e ajustar o que for necessário.

- **Atualizar JUnit em uma branch dedicada**
  - Criar uma branch específica para a migração (evitar misturar com outras mudanças grandes).
  - Rodar a suíte completa de testes, ajustando problemas conforme forem surgindo.

- **Coletar conhecimento no time**
  - Documentar no repositório (por exemplo, neste arquivo ou em um changelog) os pontos encontrados na migração.
  - Reutilizar esse conhecimento para outros serviços/projetos da organização.

### 6.6 Execução de testes no projeto (Gradle)

No contexto deste projeto (módulo `app`), os testes estão organizados em duas categorias principais, ambas rodando sobre o JUnit 6:

- Testes **unitários** e de uso de domínio/API (sem Testcontainers/Docker).
- Testes **de integração** com infraestrutura real via Testcontainers (`@Tag("integration")`).

Configuração relevante no `build.gradle` do módulo `app`:

```groovy
test {
    useJUnitPlatform {
        excludeTags 'integration'
    }
}

tasks.register('integrationTest', Test) {
    useJUnitPlatform {
        includeTags 'integration'
    }
    // demais configurações (classpath, testClassesDirs, logging, etc.)
}
```

Convenção de anotações nos testes:

- Testes que usam **Testcontainers** e precisam de Docker (por exemplo, `OrderControllerIT`, `DeliveryApplicationTests`):

  ```java
  @SpringBootTest
  @Testcontainers
  @ActiveProfiles("test")
  @Tag("integration")
  class DeliveryApplicationTests { ... }
  ```

- Demais testes (unitários/domínio) não utilizam essa tag e portanto rodam apenas em `:app:test`.

Como executar no dia a dia:

- **Apenas testes unitários (sem Docker):**

  ```bash
  ./gradlew :app:test
  ```

- **Apenas testes de integração (requer Docker/Testcontainers):**

  ```bash
  ./gradlew :app:integrationTest
  ```

- **Pipeline completo (unitários + integração + checagens):**

  ```bash
  ./gradlew :app:clean :app:test :app:integrationTest :app:check
  ```

Essa organização funciona igualmente bem com JUnit 6, aproveitando o suporte a tags do Jupiter para separar claramente os ambientes de execução.

---

## 7. Referências oficiais

- **User Guide (JUnit 6.0.1)**
  - [docs.junit.org/current/user-guide](https://docs.junit.org/current/user-guide/)

- **Release Notes (JUnit 6)**
  - [docs.junit.org/current/release-notes](https://docs.junit.org/current/release-notes/index.html)

- **Guia oficial de upgrade JUnit 5 → JUnit 6**
  - [Upgrading to JUnit 6.0](https://github.com/junit-team/junit-framework/wiki/Upgrading-to-JUnit-6.0)

Essas referências devem ser consultadas sempre que surgirem dúvidas específicas sobre comportamentos, APIs removidas ou configurações avançadas. O objetivo deste documento é servir como visão geral e guia prático inicial para o time.
