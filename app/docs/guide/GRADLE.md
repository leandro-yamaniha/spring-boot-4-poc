# Gradle – Configuração de Build, Testes e Java Toolchain

Este documento descreve as principais características de configuração do Gradle neste projeto, com foco em:

- Execução e separação de testes (`test` e `integrationTest`)
- Visualização contínua de logs de teste
- Opções de console (`--console=plain`, `--info`)
- Configuração de Java Toolchain e uso de `JAVA_HOME`

## 1. Versão do Gradle e Java

- **Gradle**: 9.2.1
- **Java alvo do projeto** (toolchain):

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

O Gradle usa o mecanismo de **Java Toolchains** para garantir que a compilação e os testes sejam executados com um JDK compatível com Java 25.

## 2. Separação de testes: `test` vs `integrationTest`

### 2.1. Testes unitários – task `test`

- Task configurada em `app/build.gradle`:

```groovy
test {
    useJUnitPlatform {
        excludeTags 'integration'
    }
    testLogging {
        showStandardStreams = true
        events 'PASSED', 'FAILED', 'SKIPPED'
    }
    finalizedBy jacocoTestReport
}
```

**Características:**

- Roda todos os testes **sem** a tag `@Tag("integration")`.
- Ativa `showStandardStreams`, exibindo **stdout/stderr** em tempo real (logs do Spring Boot, SLF4J, etc.).
- Emite eventos de teste (`PASSED`, `FAILED`, `SKIPPED`).
- Ao final, dispara o relatório de cobertura `jacocoTestReport`.

**Comando recomendado:**

```bash
./gradlew test
```

### 2.2. Testes de integração – task `integrationTest`

- Task registrada em `app/build.gradle`:

```groovy
tasks.register('integrationTest', Test) {
    description = 'Runs integration tests.'
    group = 'verification'
    useJUnitPlatform {
        includeTags 'integration'
    }
    testClassesDirs = sourceSets.test.output.classesDirs
    classpath = sourceSets.test.runtimeClasspath
    shouldRunAfter test
    testLogging {
        showStandardStreams = true
        events 'PASSED', 'FAILED', 'SKIPPED'
    }
}
```

**Características:**

- Roda apenas testes marcados com `@Tag("integration")`.
- Reaproveita o mesmo `sourceSets.test` (classes e classpath) dos testes unitários.
- `shouldRunAfter test`: mantém a ordem lógica (unitários primeiro, depois integração) quando usado em conjunto.
- Também exibe logs de teste em tempo real (`showStandardStreams = true`).

**Comandos recomendados:**

```bash
# Somente testes de integração
./gradlew integrationTest

# Unitários e depois integração
./gradlew test integrationTest
```

## 3. Visualização contínua de logs

### 3.1. `testLogging.showStandardStreams`

Tanto `test` quanto `integrationTest` possuem:

```groovy
testLogging {
    showStandardStreams = true
    events 'PASSED', 'FAILED', 'SKIPPED'
}
```

**Efeito:**

- Tudo que for escrito em `System.out` / `System.err` dentro dos testes ou da aplicação de teste (por exemplo, logs do Spring Boot, SLF4J) aparece **em tempo real** no console Gradle.

### 3.2. Opção `--console=plain`

O Gradle possui diferentes modos de console. Para uma saída mais simples e contínua, sem reescrita de linhas, pode-se usar:

```bash
./gradlew test --console=plain
./gradlew integrationTest --console=plain
```

Isso é especialmente útil quando se quer observar os logs linha a linha como em um `tail -f`.

### 3.3. Opção `--info`

A flag `--info` aumenta a verbosidade do Gradle:

```bash
./gradlew test --info
```

**Efeitos principais:**

- Exibe detalhes de resolução de dependências.
- Mostra a avaliação de projetos e tasks.
- Mostra mensagens sobre seleção de daemons e descoberta de JDKs (quando aplicável).

Para uso cotidiano, `--info` é opcional; ele é mais útil para **debug de build**.

## 4. Java Toolchain e uso de `JAVA_HOME`

### 4.1. Objetivo da configuração

O projeto utiliza **Java Toolchains** para:

- Garantir compilação com Java 25.
- Desacoplar o JDK da máquina do JDK usado no build.
- Permitir controle via `JAVA_HOME` e propriedades do Gradle.

### 4.2. Configuração em `app/build.gradle`

Trecho relevante:

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

Isso instrui o Gradle a procurar um JDK compatível com Java 25 para compilar e executar tasks Java do projeto.

### 4.3. Configuração em `gradle.properties`

Para que o Gradle considere **apenas** o JDK vindo de `JAVA_HOME` ao localizar toolchains, o projeto define em `gradle.properties` (raiz):

```properties
org.gradle.java.installations.auto-detect=false
org.gradle.java.installations.auto-download=false
org.gradle.java.installations.fromEnv=JAVA_HOME
```

**Significado:**

- `org.gradle.java.installations.auto-detect=false`
  - Desabilita varredura automática de todas as instalações de JDK do sistema.
- `org.gradle.java.installations.auto-download=false`
  - Impede o download automático de JDKs.
- `org.gradle.java.installations.fromEnv=JAVA_HOME`
  - Gradle passa a considerar **apenas** o JDK apontado pela variável de ambiente `JAVA_HOME` como fonte para toolchains.

### 4.4. Como configurar o `JAVA_HOME` (exemplo com sdkman)

Exemplo prático utilizando `sdkman` para selecionar o JDK:

```bash
sdk use java 25.0.1-graalce

# Conferir
echo $JAVA_HOME
java -version

# Rodar build/testes
./gradlew test
./gradlew integrationTest
```

Após o `sdk use`, o `JAVA_HOME` passa a apontar para algo como:

```text
/Users/<usuario>/.sdkman/candidates/java/25.0.1-graalce
```

E é esse JDK que o Gradle usará para satisfazer o `languageVersion = 25` da toolchain.

### 4.5. Gradle Daemon e troca de JDK

Ao mudar o JDK (por exemplo, via `sdk use java ...`), é recomendável parar daemons anteriores para garantir que o próximo build use o novo JDK:

```bash
./gradlew --stop
./gradlew test
```

O comando `--stop` encerra daemons antigos, e o próximo build sobe um daemon novo com o `JAVA_HOME` atual.

## 5. Resumo rápido de comandos úteis

- **Rodar unit tests com logs em tempo real**:

```bash
./gradlew test
```

- **Rodar integration tests com logs em tempo real**:

```bash
./gradlew integrationTest
```

- **Ver mais detalhes do build**:

```bash
./gradlew test --console=plain --info
```

- **Trocar o JDK usado pelo build (via sdkman)**:

```bash
sdk use java 25.0.1-graalce
./gradlew --stop
./gradlew test
```
