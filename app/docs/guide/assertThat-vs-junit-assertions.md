# assertThat (AssertJ) vs Assertivas do JUnit 5/6

Este documento compara o uso de `assertThat` (AssertJ) com as assertivas padrão do JUnit 5/6 (`Assertions.assertEquals`, `assertTrue`, etc.), analisando vantagens, desvantagens e quando usar cada um.

## 1. Visão Geral

- **JUnit 5/6 Assertions**
  - Fazem parte da própria biblioteca de testes JUnit.
  - Oferecem métodos como `assertEquals`, `assertTrue`, `assertThrows`, etc.
  - API simples e suficiente para muitos cenários.

- **AssertJ (`assertThat`)**
  - Biblioteca externa focada em assertions fluentes.
  - API rica, com centenas de métodos específicos para tipos diferentes.
  - Estilo encadeado: `assertThat(valor).isEqualTo(...).isGreaterThan(...)`.

## 2. Legibilidade e Mensagens de Erro

### JUnit 5/6

- Exemplo típico:
  - `assertEquals(expected, actual);`
- Quando a verificação falha, a mensagem padrão é mais genérica.
- Legibilidade cai quando há muitas verificações encadeadas:
  - Vários `assertEquals`/`assertTrue` em sequência sobre o mesmo objeto.

### AssertJ (`assertThat`)

- Sintaxe fluente:
  - `assertThat(pedido.getTotal()).isEqualTo(BigDecimal.TEN);`
- Mensagens de falha mais ricas e específicas por tipo.
- Facilita ler "em voz alta": "espera que total seja igual a 10".

**Resumo:** para testes que precisam ser altamente legíveis e autoexplicativos, `assertThat` costuma produzir código mais claro.

## 3. Cobertura de Tipos e Operações

### JUnit 5/6

- Oferece uma base sólida para tipos primitivos, objetos e exceções.
- Para coleções, streams e tipos mais ricos, normalmente é preciso combinar asserts genéricos:
  - `assertEquals(3, lista.size());`
  - `assertTrue(lista.contains(x));`

### AssertJ

- API específica para:
  - Coleções, mapas, `Optional`, `BigDecimal`, datas, exceções, etc.
  - Verificações encadeadas em coleções (`hasSize`, `containsExactly`, `extracting`, ...).
- Facilita expressar regras de negócio de forma direta:
  - `assertThat(itens).hasSize(3).extracting(Item::getProdutoId).containsExactly(...);`

**Resumo:** quando o teste envolve coleções, objetos complexos ou muitas propriedades, AssertJ reduz verbosidade e duplicação.

## 4. Integração com o Ecossistema Atual

No projeto atual:

- Já usamos AssertJ em vários testes (`Assertions.assertThat(...)`).
- Também usamos algumas asserts do JUnit (`assertThrows`, por exemplo, é muito útil e complementa AssertJ).

**Padrão recomendado:**

- Para **asserts de valor/estado** (equals, size, propriedades de objetos):
  - Preferir `assertThat` (AssertJ).
- Para **asserts de exceção**:
  - Tanto `assertThrows` (JUnit) quanto `assertThatThrownBy` (AssertJ) são válidos.
  - Manter consistência por classe de teste (evitar misturar estilos sem necessidade).

## 5. Vantagens e Desvantagens Resumidas

### Vantagens do AssertJ (`assertThat`)

- API fluente e legível.
- Suporte amplo a tipos complexos (coleções, mapas, opcionais, datas, BigDecimal).
- Mensagens de erro mais claras, reduzindo tempo de debug.
- Facilita testes com muitas verificações relacionadas ao mesmo objeto.

### Desvantagens do AssertJ

- Depende de biblioteca extra (já presente no projeto, então custo é baixo).
- Mais API para aprender; pode ser demais para cenários muito simples.

### Vantagens das asserts nativas do JUnit 5/6

- Nenhuma dependência adicional.
- API conhecida e padrão da comunidade.
- Ótimo para cenários simples e asserts pontuais.

### Desvantagens das asserts nativas do JUnit 5/6

- Menos expressivo para coleções e objetos complexos.
- Código pode ficar mais verboso com muitos `assertEquals`/`assertTrue` sequenciais.

## 6. Guia Prático para o Projeto

- **Novo teste de use case ou domínio:**
  - Usar `assertThat` para verificar valores, coleções e propriedades.
  - Usar JUnit (`assertThrows`) ou AssertJ (`assertThatThrownBy`) para exceções, mantendo consistência.

- **Testes existentes só com JUnit:**
  - Não é obrigatório migrar; apenas considere AssertJ ao refatorar ou estender o teste.

- **Ponto de equilíbrio:**
  - Se o teste começa a acumular muitos asserts sobre o mesmo objeto ou lista, considere trocar para AssertJ para manter o código legível e alinhado com o estilo do projeto.

## 7. Tabela de comparação detalhada (AssertJ vs JUnit 5/6)

As tabelas abaixo resumem as principais diferenças entre usar `assertThat` (AssertJ) e as asserts nativas do JUnit 5/6.

### 7.1 Visão geral

| Aspecto | AssertJ (`assertThat`) | JUnit 5/6 (`Assertions.*`) |
| --- | --- | --- |
| Estilo de API | Fluente e encadeado, por exemplo `assertThat(x).isNotNull().isGreaterThan(10)` | Funções estáticas isoladas: `assertEquals`, `assertTrue`, `assertThrows`, etc. |
| Leitura do teste | Mais próxima de linguagem natural, especialmente para coleções e objetos ricos | Pode ficar verboso quando há muitas asserts sobre o mesmo objeto ou lista |
| Cobertura de tipos | API rica para coleções, mapas, `Optional`, datas, `BigDecimal`, `Path`, etc. | API genérica, focada em igualdade, nulidade e booleanos |
| Mensagens de erro | Normalmente mais detalhadas, com diffs de coleções/mapas e indicação clara do que falhou | Mais simples; muitas vezes exige mensagem customizada para bom contexto |
| Encadeamento | Permite várias verificações encadeadas sobre o mesmo alvo | Cada verificação é uma chamada de função independente |
| Soft assertions | Possui `SoftAssertions` / `JUnitSoftAssertions` para acumular falhas | Não possui soft assertions nativas |
| Estilo BDD | Suporta `BDDAssertions.then(...)` para estilo given/when/then | Não possui API específica de BDD |
| Dependências | Requer dependência externa (AssertJ) | Já faz parte do JUnit 5/6 (nenhuma dependência extra) |

### 7.2 O que o AssertJ oferece a mais que o JUnit 5/6

| Categoria | AssertJ (`assertThat`) | Situação com JUnit 5/6 |
| --- | --- | --- |
| Coleções e mapas | Métodos como `hasSize`, `containsExactly`, `containsOnly`, `containsExactlyInAnyOrder`, `containsSubsequence`, `doesNotContain`, etc. | Normalmente combinações de `assertEquals`, `assertIterableEquals`, `assertTrue(lista.contains(...))`, mais verbosas |
| Objetos complexos | `extracting`, `filteredOn`, `usingRecursiveComparison()` facilitam testar propriedades aninhadas e objetos ricos | Geralmente é necessário escrever código manual para montar o esperado e comparar campo a campo |
| Tipos de domínio modernos | Assertions específicas para `Optional`, datas (`OffsetDateTime`, `LocalDateTime`), `Path`, `File`, `BigDecimal`, entre outros | Usa assertions genéricas (`assertEquals`, `assertNotNull`, `assertTrue`) sem semântica específica para o tipo |
| Encadeamento de regras | Várias verificações sobre o mesmo alvo em uma única expressão de teste | Diversas chamadas de assertion espalhadas no método de teste |
| Soft assertions | `SoftAssertions` permite ver várias falhas de uma vez em vez de parar na primeira | Requer `assertAll` com lambdas ou múltiplos testes para comportamento semelhante |

### 7.3 O que o JUnit 5/6 oferece que o AssertJ não substitui

| Categoria | JUnit 5/6 | Relação com AssertJ |
| --- | --- | --- |
| Assumptions | `Assumptions.assumeTrue/assumeFalse/assumeThat` para ignorar testes em determinadas condições | AssertJ não oferece assumptions; continuamos usando as do JUnit para controle de execução dos testes |
| Controle de fluxo com exceções | `assertThrows` e `assertDoesNotThrow` definem claramente quando o teste deve falhar ou passar com base em exceções | AssertJ tem assertions de exceção (`assertThatThrownBy` etc.), mas não substitui o papel do `assertThrows` no fluxo do teste |
| Integração com o framework | Assertions são parte oficial do JUnit Jupiter (5/6), integradas ao ecossistema (IDE, relatórios, exemplos oficiais) | AssertJ funciona em cima de qualquer framework de teste e complementa as asserts nativas |
| `assertAll` | Permite agrupar várias assertions em lambdas, reportando todas as falhas de uma vez | Alternativa ao uso de `SoftAssertions`; escolha depende de estilo do time e do teste |
