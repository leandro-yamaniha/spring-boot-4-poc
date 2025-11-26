# assertThat (AssertJ) vs Assertivas do JUnit 5

Este documento compara o uso de `assertThat` (AssertJ) com as assertivas padrão do JUnit 5 (`Assertions.assertEquals`, `assertTrue`, etc.), analisando vantagens, desvantagens e quando usar cada um.

## 1. Visão Geral

- **JUnit 5 Assertions**
  - Fazem parte da própria biblioteca de testes JUnit.
  - Oferecem métodos como `assertEquals`, `assertTrue`, `assertThrows`, etc.
  - API simples e suficiente para muitos cenários.

- **AssertJ (`assertThat`)**
  - Biblioteca externa focada em assertions fluentes.
  - API rica, com centenas de métodos específicos para tipos diferentes.
  - Estilo encadeado: `assertThat(valor).isEqualTo(...).isGreaterThan(...)`.

## 2. Legibilidade e Mensagens de Erro

### JUnit 5

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

### JUnit 5

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

### Vantagens das asserts do JUnit 5

- Nenhuma dependência adicional.
- API conhecida e padrão da comunidade.
- Ótimo para cenários simples e asserts pontuais.

### Desvantagens das asserts do JUnit 5

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
