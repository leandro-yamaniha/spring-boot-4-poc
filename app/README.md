# Módulo `app` – Backend de Entrega de Pedidos

Este módulo contém o backend Spring Boot responsável pelo core de pedidos.

- **Java**: 25 (toolchain Gradle)
- **Spring Boot**: 4.0.0
- **Build**: Gradle
- **Arquitetura**: Camadas (Layered) + DDD leve, com princípios de Hexagonal aplicados onde fizer sentido
- **Stack principal**:
  - Spring Web, Spring Data JPA, Spring Cache + Redis, Actuator
  - SpringDoc OpenAPI 3 (Swagger UI)
  - MapStruct, JUnit 6, Testcontainers, RestAssured, Cucumber, Instancio
  - Checkstyle, JaCoCo, SonarQube (integração futura)

---

## 📚 Documentação da API

A API é documentada automaticamente usando **SpringDoc OpenAPI 3** (Swagger).

### Endpoints de documentação:

- **Swagger UI** (interface interativa): <http://localhost:8080/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:8080/v3/api-docs>
- **OpenAPI YAML**: <http://localhost:8080/v3/api-docs.yaml>

### Como usar:

1. Inicie a aplicação (`./gradlew :app:bootRun`)
2. Acesse <http://localhost:8080/swagger-ui.html> no navegador
3. Explore e teste os endpoints diretamente na interface

A documentação é gerada automaticamente a partir dos controllers e anotações do código.

---

## Estrutura de Pacotes

```
com.poc.delivery/
├── api/              → Controllers REST, DTOs, validação de entrada, @RestControllerAdvice
├── application/      → Casos de uso, serviços de aplicação, orquestração de domínio
├── domain/           → Entidades, agregados, value objects, regras de negócio
└── infrastructure/   → Repositórios JPA, integrações externas, mapeadores
```

**Princípios**:
- **Domínio** contém as regras de negócio e é independente de frameworks quando possível.
- **Application** orquestra o domínio e coordena transações.
- **API** expõe endpoints REST e delega para a camada de aplicação.
- **Infrastructure** implementa detalhes técnicos (persistência, integrações).

Para mais detalhes, consulte `planejamento/analise-arquiteturas-backend.md`.

---

## Premissas de Desenvolvimento

### Testes Unitários (Obrigatório)

**Toda implementação de código de produção DEVE ser acompanhada de testes unitários usando JUnit 6 (Jupiter).**

- **Framework**: JUnit 6 (Jupiter) - lançado em setembro de 2024
- **Cobertura mínima**: 90% (JaCoCo)
- **Ferramentas auxiliares**:
  - **Instancio**: para criação de instâncias de teste
  - **Mockito**: para mocks (incluído no `spring-boot-starter-test`)
  - **AssertJ**: para assertions fluentes (incluído no `spring-boot-starter-test`)

**Regras**:
1. Testes unitários devem cobrir:
   - Regras de negócio no domínio
   - Lógica de aplicação (casos de uso)
   - Validações e mapeamentos
2. Testes devem ser **determinísticos** (sem dependências de tempo real, rede ou ordem de execução)
3. Usar **mocks/stubs** para dependências externas
4. Seguir padrão **AAA** (Arrange, Act, Assert)

**Exemplo**:
```java
@Test
void deveCriarPedidoComSucessoQuandoDadosValidos() {
    // Arrange
    var pedido = Instancio.create(Pedido.class);
    
    // Act
    var resultado = pedidoService.criar(pedido);
    
    // Assert
    assertThat(resultado).isNotNull();
    assertThat(resultado.getId()).isPositive();
}
```

Para testes de integração, consulte a seção de **Testcontainers** abaixo.

---

## Comandos úteis

### Build e testes

- **Compilar e rodar testes unitários do módulo `app`**

```bash
./gradlew :app:clean :app:test
```

- **Rodar apenas testes de integração do módulo `app` (@Tag("integration"))**

```bash
./gradlew :app:integrationTest
```

- **Build completo (compila + checkstyle + testes)**

```bash
./gradlew :app:clean :app:build
```

### Relatórios de Qualidade

#### Checkstyle (Qualidade de Código)

Executa verificação de padrões de código (35 regras ativas):

```bash
./gradlew :app:checkstyleMain :app:checkstyleTest
```

**Visualizar relatórios:**

```bash
# Relatório HTML (principal)
open app/build/reports/checkstyle/main.html

# Relatório HTML (testes)
open app/build/reports/checkstyle/test.html

# Relatório XML (para CI/CD)
cat app/build/reports/checkstyle/main.xml
```

**Regras ativas:**
- Nomenclatura (PascalCase, camelCase, UPPER_SNAKE_CASE)
- Complexidade ciclomática ≤ 10
- Métodos ≤ 50 linhas
- Parâmetros ≤ 5
- Arquivos ≤ 500 linhas
- Tratamento de erros (sem catch vazio, exceções específicas)
- Legibilidade (imports, @Override, declarações)
- Segurança (sem System.out/err)

#### JaCoCo (Cobertura de Testes)

Gera relatório de cobertura de código:

```bash
./gradlew :app:test :app:jacocoTestReport
```

**Visualizar relatórios:**

```bash
# Relatório HTML (navegável) - macOS
open app/build/reports/jacoco/test/html/index.html

# Relatório HTML (navegável) - Linux
xdg-open app/build/reports/jacoco/test/html/index.html

# Ou abra diretamente no navegador:
# file:///caminho-completo/spring-boot-4-poc/app/build/reports/jacoco/test/html/index.html

# Verificar se o relatório foi gerado
ls -la app/build/reports/jacoco/test/html/index.html

# Relatório XML (para CI/CD e SonarQube)
cat app/build/reports/jacoco/test/jacocoTestReport.xml
```

**⚠️ Importante:** O relatório só é gerado após executar os testes. Se o arquivo não existir, execute:
```bash
./gradlew clean test jacocoTestReport
```

**Métricas disponíveis:**
- Cobertura de linhas
- Cobertura de branches (condicionais)
- Cobertura de métodos
- Cobertura de classes
- Complexidade ciclomática coberta

### Executar a aplicação com profile `local`

```bash
./gradlew :app:bootRun --args='--spring.profiles.active=local'
```

Certifique-se de que o PostgreSQL e o Redis locais (ou via Docker Compose) estão compatíveis com as configurações de `application-local.yml`.

### Parar a aplicação

Se a aplicação estiver rodando na porta 8080, use:

```bash
lsof -ti:8080 | xargs kill -9
```

Ou pare todos os daemons do Gradle:

```bash
./gradlew --stop
```

---

## Referências

- **Java (OpenJDK)**: <https://openjdk.org/>
- **Spring Boot**: <https://spring.io/projects/spring-boot>
- **Gradle**: <https://gradle.org/>
- **JUnit 6**: <https://junit.org/>
- **Testcontainers**: <https://www.testcontainers.org/>
- **RestAssured**: <https://rest-assured.io/>
- **Cucumber**: <https://cucumber.io/>
- **MapStruct**: <https://mapstruct.org/>
- **Instancio**: <https://www.instancio.org/>
- **Checkstyle**: <https://checkstyle.org/>
- **JaCoCo**: <https://www.jacoco.org/jacoco/>
- **SonarQube**: <https://www.sonarqube.org/>
