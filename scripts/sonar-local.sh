#!/bin/bash

# Script para executar análise SonarQube local com Docker
# Requisitos: Docker instalado e rodando

set -e  # Para na primeira falha

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
SONAR_CONTAINER_NAME="sonarqube-local"
SONAR_PORT=9000
SONAR_HOST="http://localhost:${SONAR_PORT}"
SONAR_PROJECT_KEY="delivery-backend"
SONAR_TOKEN=""
MAX_WAIT_TIME=300  # 5 minutos

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  SonarQube Local - Análise de Código${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Função para verificar se o Docker está rodando
check_docker() {
    echo -e "${YELLOW}[1/6] Verificando Docker...${NC}"
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}❌ Docker não está rodando. Inicie o Docker e tente novamente.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Docker está rodando${NC}"
    echo ""
}

# Função para iniciar o SonarQube
start_sonarqube() {
    echo -e "${YELLOW}[2/6] Iniciando SonarQube...${NC}"
    
    # Verifica se o container já existe
    if docker ps -a --format '{{.Names}}' | grep -q "^${SONAR_CONTAINER_NAME}$"; then
        echo "Container ${SONAR_CONTAINER_NAME} já existe."
        
        # Verifica se está rodando
        if docker ps --format '{{.Names}}' | grep -q "^${SONAR_CONTAINER_NAME}$"; then
            echo -e "${GREEN}✅ SonarQube já está rodando${NC}"
        else
            echo "Iniciando container existente..."
            docker start ${SONAR_CONTAINER_NAME}
            echo -e "${GREEN}✅ Container iniciado${NC}"
        fi
    else
        echo "Criando novo container SonarQube..."
        docker run -d \
            --name ${SONAR_CONTAINER_NAME} \
            -p ${SONAR_PORT}:9000 \
            -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
            sonarqube:latest
        echo -e "${GREEN}✅ Container criado e iniciado${NC}"
    fi
    echo ""
}

# Função para aguardar o SonarQube ficar pronto
wait_sonarqube() {
    echo -e "${YELLOW}[3/6] Aguardando SonarQube ficar pronto...${NC}"
    echo "Isso pode levar alguns minutos na primeira execução..."
    
    local elapsed=0
    while [ $elapsed -lt $MAX_WAIT_TIME ]; do
        if curl -s "${SONAR_HOST}/api/system/status" | grep -q '"status":"UP"'; then
            echo -e "${GREEN}✅ SonarQube está pronto!${NC}"
            echo ""
            return 0
        fi
        
        echo -n "."
        sleep 5
        elapsed=$((elapsed + 5))
    done
    
    echo -e "${RED}❌ Timeout: SonarQube não ficou pronto em ${MAX_WAIT_TIME}s${NC}"
    exit 1
}

# Função para gerar token de autenticação
generate_token() {
    echo -e "${YELLOW}[4/6] Gerando token de autenticação...${NC}"
    
    # Credenciais configuradas
    local admin_user="admin"
    local admin_pass="d3l1v3ry#Pr0j3ct"
    
    # Tenta gerar token
    SONAR_TOKEN=$(curl -s -u ${admin_user}:${admin_pass} \
        -X POST "${SONAR_HOST}/api/user_tokens/generate?name=local-analysis-$(date +%s)" \
        | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    
    if [ -z "$SONAR_TOKEN" ]; then
        echo -e "${YELLOW}⚠️  Não foi possível gerar token automaticamente${NC}"
        echo "Acesse ${SONAR_HOST} e gere um token manualmente:"
        echo "  1. Login: admin / d3l1v3ry#Pr0j3ct"
        echo "  2. My Account > Security > Generate Token"
        echo ""
        read -p "Cole o token aqui: " SONAR_TOKEN
    else
        echo -e "${GREEN}✅ Token gerado com sucesso${NC}"
    fi
    echo ""
}

# Função para executar o build
run_build() {
    echo -e "${YELLOW}[5/6] Executando build do projeto...${NC}"
    
    cd "$(dirname "$0")/.."
    
    ./gradlew clean build
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Build executado com sucesso${NC}"
    else
        echo -e "${RED}❌ Build falhou${NC}"
        exit 1
    fi
    echo ""
}

# Função para instalar SonarScanner CLI
install_sonar_scanner() {
    local scanner_dir="$HOME/.sonar-scanner"
    local scanner_version="6.2.1.4610"
    local scanner_zip="sonar-scanner-cli-${scanner_version}.zip"
    
    if [ -f "${scanner_dir}/bin/sonar-scanner" ]; then
        echo -e "${GREEN}✅ SonarScanner CLI já instalado${NC}"
        return 0
    fi
    
    echo -e "${YELLOW}📥 Baixando SonarScanner CLI...${NC}"
    
    mkdir -p "${scanner_dir}"
    cd "${scanner_dir}"
    
    curl -sL "https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/${scanner_zip}" -o "${scanner_zip}"
    unzip -q "${scanner_zip}"
    mv sonar-scanner-${scanner_version}/* .
    rm -rf sonar-scanner-${scanner_version} "${scanner_zip}"
    
    echo -e "${GREEN}✅ SonarScanner CLI instalado${NC}"
}

# Função para executar análise do SonarQube
run_sonar_analysis() {
    echo -e "${YELLOW}[6/6] Executando análise SonarQube...${NC}"
    
    # Instalar SonarScanner se necessário
    install_sonar_scanner
    
    local scanner_bin="$HOME/.sonar-scanner/bin/sonar-scanner"
    local project_dir="$(dirname "$0")/.."
    
    # Voltar para o diretório do projeto
    cd "${project_dir}"
    
    # Executar análise usando SonarScanner CLI
    ${scanner_bin} \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN} \
        -Dsonar.qualitygate.wait=true \
        -Dsonar.qualitygate.timeout=300
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        # Verificar se há issues não resolvidos
        echo ""
        echo -e "${YELLOW}Verificando issues não resolvidos...${NC}"
        
        local admin_user="admin"
        local admin_pass="d3l1v3ry#Pr0j3ct"
        local issues_count=$(curl -s -u ${admin_user}:${admin_pass} \
            "${SONAR_HOST}/api/issues/search?componentKeys=${SONAR_PROJECT_KEY}&resolved=false" \
            | grep -o '"total":[0-9]*' | head -1 | cut -d':' -f2)
        
        if [ "$issues_count" != "0" ]; then
            echo ""
            echo -e "${RED}========================================${NC}"
            echo -e "${RED}  ❌ Análise falhou!${NC}"
            echo -e "${RED}========================================${NC}"
            echo ""
            echo -e "${RED}Issues encontrados: ${issues_count}${NC}"
            echo ""
            echo "Quality Gate exige:"
            echo "  - Issues: 0 (encontrado: ${issues_count})"
            echo ""
            echo -e "📊 Veja detalhes em: ${BLUE}${SONAR_HOST}/dashboard?id=${SONAR_PROJECT_KEY}${NC}"
            echo ""
            exit 1
        fi
        
        echo -e "${GREEN}✅ Nenhum issue encontrado${NC}"
        echo ""
        echo -e "${GREEN}========================================${NC}"
        echo -e "${GREEN}  ✅ Análise concluída com sucesso!${NC}"
        echo -e "${GREEN}========================================${NC}"
        echo ""
        echo -e "📊 Relatório disponível em: ${BLUE}${SONAR_HOST}/dashboard?id=${SONAR_PROJECT_KEY}${NC}"
        echo ""
        echo -e "${GREEN}Quality Gate: PASSED ✓${NC}"
        echo -e "  - Cobertura: 100% ✓"
        echo -e "  - Bugs: 0 ✓"
        echo -e "  - Code Smells: 0 ✓"
        echo -e "  - Issues: 0 ✓"
        echo ""
    else
        echo ""
        echo -e "${RED}========================================${NC}"
        echo -e "${RED}  ❌ Análise falhou!${NC}"
        echo -e "${RED}========================================${NC}"
        echo ""
        echo "Possíveis causas:"
        echo "  - Quality Gate não passou"
        echo "  - Cobertura < 100%"
        echo "  - Code Smells > 0"
        echo "  - Bugs > 0"
        echo "  - Issues > 0"
        echo ""
        echo -e "📊 Veja detalhes em: ${BLUE}${SONAR_HOST}/dashboard?id=${SONAR_PROJECT_KEY}${NC}"
        echo ""
        exit 1
    fi
}

# Função para configurar Quality Gate
configure_quality_gate() {
    echo -e "${YELLOW}Configurando Quality Gate rigoroso...${NC}"
    
    local admin_user="admin"
    local admin_pass="d3l1v3ry#Pr0j3ct"
    local qg_name="Zero Tolerance"
    
    # Verificar se Quality Gate já existe
    local existing_qg=$(curl -s -u ${admin_user}:${admin_pass} "${SONAR_HOST}/api/qualitygates/list" | grep -o "\"name\":\"${qg_name}\"")
    
    if [ -n "$existing_qg" ]; then
        echo -e "${GREEN}✅ Quality Gate '${qg_name}' já existe${NC}"
        echo ""
        return 0
    fi
    
    # Criar Quality Gate
    local create_response=$(curl -s -u ${admin_user}:${admin_pass} -X POST "${SONAR_HOST}/api/qualitygates/create?name=${qg_name}")
    
    # Obter ID do Quality Gate criado
    local qg_id=$(echo "$create_response" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    
    if [ -n "$qg_id" ]; then
        # Adicionar condições
        curl -s -u ${admin_user}:${admin_pass} -X POST "${SONAR_HOST}/api/qualitygates/create_condition?gateId=${qg_id}&metric=coverage&op=LT&error=100" > /dev/null
        curl -s -u ${admin_user}:${admin_pass} -X POST "${SONAR_HOST}/api/qualitygates/create_condition?gateId=${qg_id}&metric=bugs&op=GT&error=0" > /dev/null
        curl -s -u ${admin_user}:${admin_pass} -X POST "${SONAR_HOST}/api/qualitygates/create_condition?gateId=${qg_id}&metric=code_smells&op=GT&error=0" > /dev/null
        
        # Definir como padrão
        curl -s -u ${admin_user}:${admin_pass} -X POST "${SONAR_HOST}/api/qualitygates/set_as_default?id=${qg_id}" > /dev/null
        
        echo -e "${GREEN}✅ Quality Gate configurado com sucesso${NC}"
    else
        echo -e "${YELLOW}⚠️  Não foi possível configurar Quality Gate automaticamente${NC}"
        echo "Configure manualmente em: ${SONAR_HOST}/quality_gates"
    fi
    echo ""
}

# Função principal
main() {
    check_docker
    start_sonarqube
    wait_sonarqube
    configure_quality_gate
    generate_token
    run_build
    run_sonar_analysis
}

# Executar
main
