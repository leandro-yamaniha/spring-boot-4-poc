# Scripts de Automação

Este diretório contém scripts para automação de tarefas do projeto.

## 📊 sonar-local.sh

Script para executar análise SonarQube local com Docker.

### Pré-requisitos

- Docker instalado e rodando
- Gradle configurado

### O que o script faz

1. ✅ Verifica se Docker está rodando
2. 🐳 Inicia container SonarQube (ou usa existente)
3. ⏳ Aguarda SonarQube ficar pronto (até 5 minutos)
4. 🔑 Gera token de autenticação automaticamente
5. 🏗️ Executa `./gradlew clean build`
6. 📊 Executa análise SonarQube com Quality Gate

### Quality Gate Configurado

**Zero Tolerance** - Padrões rigorosos:

- ✅ **Cobertura:** 100% (falha se < 100%)
- ✅ **Bugs:** 0 (falha se > 0)
- ✅ **Code Smells:** 0 (falha se > 0)

### Como usar

```bash
# Executar análise completa
./scripts/sonar-local.sh
```

### Primeira execução

Na primeira vez, o script irá:
- Baixar a imagem Docker do SonarQube (~500MB)
- Iniciar o container (pode levar 2-3 minutos)
- Configurar Quality Gate automaticamente

### Execuções subsequentes

O script reutiliza o container existente, sendo muito mais rápido.

### Acessar SonarQube

Após a execução, acesse:
- **URL:** http://localhost:9000
- **Login:** admin
- **Senha:** admin (primeira vez, será solicitado trocar)

### Parar o SonarQube

```bash
# Parar container
docker stop sonarqube-local

# Remover container (se quiser recomeçar do zero)
docker rm sonarqube-local
```

### Troubleshooting

**Docker não está rodando:**
```
❌ Docker não está rodando. Inicie o Docker e tente novamente.
```
Solução: Inicie o Docker Desktop

**Timeout ao aguardar SonarQube:**
```
❌ Timeout: SonarQube não ficou pronto em 300s
```
Solução: Aguarde mais tempo ou reinicie o container

**Build falhou:**
```
❌ Build falhou
```
Solução: Corrija os erros de compilação/testes antes de rodar o SonarQube

**Quality Gate falhou:**
```
❌ Análise falhou!
Possíveis causas:
  - Quality Gate não passou
  - Cobertura < 100%
  - Code Smells > 0
  - Bugs > 0
```
Solução: Acesse o dashboard do SonarQube para ver detalhes e corrija os problemas

### Logs do Container

```bash
# Ver logs do SonarQube
docker logs sonarqube-local

# Seguir logs em tempo real
docker logs -f sonarqube-local
```

### Integração com CI/CD

Este script pode ser usado em pipelines de CI/CD:

```yaml
# Exemplo GitHub Actions
- name: Run SonarQube Analysis
  run: ./scripts/sonar-local.sh
```

### Variáveis de Ambiente

Você pode customizar o comportamento do script:

```bash
# Usar porta diferente
SONAR_PORT=9001 ./scripts/sonar-local.sh

# Usar token existente
SONAR_TOKEN=seu-token ./scripts/sonar-local.sh
```

## 📈 version-bump.sh

Script para atualizar a **versão** do backend em `app/build.gradle` seguindo o versionamento semântico definido no `agents.md`.

### O que o script faz

- Lê a linha `version = 'X.Y.Z[-SNAPSHOT]'` em `app/build.gradle`.
- Calcula a nova versão de acordo com o tipo de bump (`major`, `minor` ou `patch`).
- Mantém o sufixo `-SNAPSHOT`, se presente.
- Atualiza o arquivo `app/build.gradle` com a nova versão.

### Mapeamento com o agents.md

- `major` → mudanças com **BREAKING CHANGE**.
- `minor` → commits do tipo **feat:** (novas funcionalidades compatíveis).
- `patch` → commits do tipo **fix:** (correções compatíveis).

> Observação: commits `chore:`, `docs:`, `style:`, `refactor:`, `test:` em geral **não** exigem bump de versão.

### Como usar

Dar permissão de execução (uma única vez):

```bash
chmod +x scripts/version-bump.sh
```

Depois, de acordo com o tipo de mudança:

```bash
# BREAKING CHANGE (MAJOR)
./scripts/version-bump.sh major

# Nova feature compatível (MINOR)
./scripts/version-bump.sh minor

# Bugfix compatível (PATCH)
./scripts/version-bump.sh patch
```

Exemplo: se a versão atual for `0.0.1-SNAPSHOT`:

- `./scripts/version-bump.sh minor` → `0.1.0-SNAPSHOT`
- `./scripts/version-bump.sh patch` → `0.0.2-SNAPSHOT`

Após rodar o script, revise o `CHANGELOG.md` e o `README.md` conforme orientações do `agents.md` antes de criar o commit.
