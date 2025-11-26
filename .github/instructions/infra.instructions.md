# Infra Instructions

Regras para o Copilot ao sugerir scripts e infraestrutura neste repositório.

## Scripts (shell)

- Usar `bash` com `set -e` ou `set -euo pipefail` em scripts críticos.
- Sempre detectar diretório base com padrão seguro:
  - `SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"`
  - `PROJECT_DIR="${SCRIPT_DIR}/.."`
- Não hardcodar caminhos absolutos; trabalhar relativo à raiz do projeto.
- Mensagens de saída claras, em português, indicando passos e erros.

## Sonar e Qualidade

- Reutilizar o padrão do `scripts/sonar-local.sh`:
  - Verificar Docker.
  - Subir container SonarQube se necessário.
  - Rodar `./gradlew clean build` antes da análise.
  - Chamar o `sonar-scanner` a partir da raiz do projeto.
  - Consultar a API do Sonar e falhar se houver issues não resolvidas.
- Quando listar issues ou violações, incluir ao menos:
  - Severidade, tipo, arquivo, linha, mensagem.

## Checkstyle / Jacoco

- Não alterar `config/checkstyle/checkstyle.xml` de forma a relaxar regras principais (sem combinar com o time).
- Ao sugerir mudanças em cobertura de testes:
  - Usar testes adicionais antes de mexer em limites do Jacoco.

## Segurança e Logs

- Em scripts ou configs, evitar expor credenciais sensíveis em texto plano.
  - Preferir variáveis de ambiente quando possível.
- Para logs HTTP:
  - Seguir o padrão de `HttpRequestLoggingFilter` (mascarar headers como `authorization` e `cookie`).

## Docker / Infra Local

- Se sugerir novos serviços Docker, manter coerência com o padrão atual:
  - Usar imagens oficiais e leves (ex.: `postgres:16-alpine`).
  - Configurar portas e volumes de forma explícita.
- Scripts não devem depender de ferramentas externas não documentadas (instalar via README se for o caso).
