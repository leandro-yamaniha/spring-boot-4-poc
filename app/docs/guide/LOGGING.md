# Logging – Padrão de Logs e Códigos de Evento

Este documento descreve o padrão de logging utilizado neste projeto, com foco em:

- Padronização de códigos de evento (`LogEvent`)
- Logging centralizado de HTTP request/response
- Logs de domínio no fluxo de criação de pedido
- Logs padronizados de erros de negócio e erros inesperados

## 1. Enum `LogEvent` – Códigos de Evento

Local: `app/src/main/java/com/poc/delivery/common/logging/LogEvent.java`

```java
public enum LogEvent {

    HTTP_REQUEST_COMPLETED("HTTP-001"),
    ORDER_REQUEST_RECEIVED("ORD-000"),
    ORDER_CREATED("ORD-001"),
    ORDER_VALIDATION_FAILED("ORD-010"),
    ORDER_UNEXPECTED_ERROR("ORD-500");

    private final String code;

    LogEvent(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
```

### 1.1. Semântica dos códigos

- `HTTP-001` – Logging de requições/respostas HTTP concluídas
- `ORD-000` – Reserva para logs de entrada no fluxo de pedido (não utilizado atualmente no controlador)
- `ORD-001` – Eventos de criação/persistência de pedido
- `ORD-010` – Erros de validação de negócio ou entrada relacionados a pedidos
- `ORD-500` – Erros inesperados no fluxo de pedidos (equivalente a erro 500)

Esses códigos podem ser usados em painéis, buscas de log e documentação de sequência para correlacionar eventos.

## 2. Logging HTTP – `HttpRequestLoggingFilter`

Local: `app/src/main/java/com/poc/delivery/common/logging/HttpRequestLoggingFilter.java`

### 2.1. Responsabilidade

- Centralizar o logging de HTTP **para todas as requisições**.
- Capturar e logar:
  - Método HTTP
  - Caminho (`URI`)
  - Headers (com mascaramento de dados sensíveis)
  - Corpo de request/response (quando seguro)
  - Status HTTP e duração da requisição em ms

### 2.2. Implementação principal

```java
@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final int MAX_BODY_LENGTH = 2000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, MAX_BODY_LENGTH);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logRequest(requestWrapper);
            logResponse(responseWrapper, duration);
            responseWrapper.copyBodyToResponse();
        }
    }
}
```

### 2.3. Logging de request

```java
private void logRequest(ContentCachingRequestWrapper requestWrapper) {
    if (!LOGGER.isInfoEnabled()) {
        return;
    }
    String method = requestWrapper.getMethod();
    String path = requestWrapper.getRequestURI();
    String body = extractBody(requestWrapper.getContentAsByteArray(), requestWrapper.getContentType());
    Map<String, String> headers = extractRequestHeaders(requestWrapper);
    LOGGER.info("[{}] HTTP request method={}, path={}, headers={}, body={}",
        LogEvent.HTTP_REQUEST_COMPLETED.code(), method, path, headers, body);
}
```

**Características:**

- Usa `ContentCachingRequestWrapper` para capturar o corpo **após** ser lido pelo Spring (controller/converters).
- Extrai headers em um `LinkedHashMap`, mantendo a ordem.
- Mascaramento de dados sensíveis:
  - `Authorization` → `<redacted>`
  - `Cookie` → `<redacted>`
- Usa o código `HTTP-001` em todos os logs de request.

### 2.4. Logging de response

```java
private void logResponse(ContentCachingResponseWrapper responseWrapper, long duration) {
    if (!LOGGER.isInfoEnabled()) {
        return;
    }
    int status = responseWrapper.getStatus();
    String body = extractBody(responseWrapper.getContentAsByteArray(), responseWrapper.getContentType());
    Map<String, String> headers = extractResponseHeaders(responseWrapper);
    LOGGER.info("[{}] HTTP response status={}, durationMs={}, headers={}, body={}",
        LogEvent.HTTP_REQUEST_COMPLETED.code(), status, duration, headers, body);
}
```

**Características:**

- Usa `ContentCachingResponseWrapper` para capturar o corpo escrito pelo controller/handler.
- Extrai headers com mascaramento de `Set-Cookie` → `<redacted>`.
- Loga:
  - `status`
  - `durationMs` (tempo total entre entrada e saída no filtro)
  - headers
  - body (quando permitido)
- Usa também o código `HTTP-001` para respostas.

### 2.5. Política de corpo (body) logado

```java
private String extractBody(byte[] content, String contentType) {
    if (content == null || content.length == 0) {
        return "";
    }
    if (contentType == null || (!contentType.contains("application/json") && !contentType.startsWith("text"))) {
        return "<not-logged>";
    }
    String body = new String(content, StandardCharsets.UTF_8);
    if (body.length() > MAX_BODY_LENGTH) {
        return body.substring(0, MAX_BODY_LENGTH) + "...";
    }
    return body;
}
```

Regras:

- Corpo vazio → string vazia.
- Apenas loga corpo para:
  - `application/json`
  - tipos que começam com `text` (por exemplo, `text/plain`).
- Qualquer outro `contentType` → `"<not-logged>"`.
- Truncamento para no máximo `MAX_BODY_LENGTH` (2000 caracteres), com sufixo `"..."`.

## 3. Logs de Domínio – Criação de Pedido

### 3.1. `CreateOrderUseCase` – Persistência de pedido

Local: `app/src/main/java/com/poc/delivery/domain/usecase/CreateOrderUseCase.java`

Trecho relevante:

```java
LOGGER.info("[{}] Pedido persistido com id={}", LogEvent.ORDER_CREATED.code(), savedPedido.getId());
```

**Semântica:**

- Marca o momento em que o pedido foi persistido com sucesso na base de dados.
- Usa o código `ORD-001` para indicar evento de criação/persistência.

### 3.2. `OrderController` – Resposta de criação de pedido

Local: `app/src/main/java/com/poc/delivery/api/OrderController.java`

Trecho relevante:

```java
PedidoResponse response = createOrderUseCase.execute(request);
LOGGER.info("[{}] Pedido criado com sucesso, id={}", LogEvent.ORDER_CREATED.code(), response.id());
return ResponseEntity.status(HttpStatus.CREATED).body(response);
```

**Semântica:**

- Marca o sucesso da operação HTTP de criação de pedido.
- Usa o mesmo código `ORD-001`, permitindo correlacionar:
  - Log de persistência (`CreateOrderUseCase`).
  - Log de resposta HTTP (`OrderController`).

### 3.3. Sequência típica de logs na criação de pedido

Para uma requisição `POST /api/v1/orders` bem-sucedida, a sequência esperada é:

1. `[HTTP-001]` – Request HTTP (filtro) – método, path, headers, body.
2. `[ORD-001]` – Persistência de pedido no `CreateOrderUseCase`.
3. `[ORD-001]` – Sucesso de criação no `OrderController`.
4. `[HTTP-001]` – Response HTTP (filtro) – status 201, headers, body.

Esses eventos podem ser usados em diagramas de sequência e troubleshooting para acompanhar o fluxo ponta a ponta.

## 4. Logs de Erro – `GlobalExceptionHandler`

Local: `app/src/main/java/com/poc/delivery/api/GlobalExceptionHandler.java`

### 4.1. Erros inesperados (500)

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {
    LOGGER.error("[{}] Erro inesperado no processamento da requisicao", LogEvent.ORDER_UNEXPECTED_ERROR.code(), ex);
    ApiError error = new ApiError("INTERNAL_ERROR", "An unexpected error occurred");
    ApiErrorResponse body = new ApiErrorResponse(error);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
}
```

- Usa o código `ORD-500` (`ORDER_UNEXPECTED_ERROR`).
- Log em nível `ERROR` com stacktrace.
- Response padronizada com `code = INTERNAL_ERROR`.

### 4.2. Erros de negócio (`IllegalArgumentException`)

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    LOGGER.warn("[{}] Erro de validacao de negocio: {}", LogEvent.ORDER_VALIDATION_FAILED.code(), ex.getMessage());
    ApiError error = new ApiError("BAD_REQUEST", ex.getMessage());
    ApiErrorResponse body = new ApiErrorResponse(error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
}
```

- Usa o código `ORD-010` (`ORDER_VALIDATION_FAILED`).
- Nível de log `WARN`.
- Response `400 BAD_REQUEST` com mensagem de negócio.

### 4.3. Erros de validação de entrada (`MethodArgumentNotValidException`)

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getAllErrors().stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("Validation failed");
    LOGGER.warn("[{}] Erro de validacao de entrada: {}", LogEvent.ORDER_VALIDATION_FAILED.code(), message);
    ApiError error = new ApiError("BAD_REQUEST", message);
    ApiErrorResponse body = new ApiErrorResponse(error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
}
```

- Também usa `ORD-010`.
- Log em `WARN` com a mensagem amigável vinda das constraints de validação.
- Response `400 BAD_REQUEST` com mensagem legível.

## 5. Boas práticas adotadas

- **Códigos de evento fixos** (`LogEvent`) para facilitar:
  - Busca em ferramentas de log.
  - Correlação com diagramas de sequência.
  - Observabilidade e alertas baseados em códigos.
- **Centralização de HTTP logging** via filtro:
  - Evita duplicação de logs em cada controller.
  - Garante padrão consistente para todos os endpoints.
- **Mascaramento de dados sensíveis** (Authorization, Cookie, Set-Cookie).
- **Truncamento e filtragem de corpo** para evitar logs gigantes ou binários.
- **Separação entre logs de domínio e de infraestrutura**:
  - Filtro trata HTTP.
  - Use case e controller tratam eventos de negócio (`ORD-001`).
  - GlobalExceptionHandler trata erros (`ORD-010`, `ORD-500`).
