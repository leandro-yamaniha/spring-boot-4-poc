package com.poc.delivery.common.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

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

    private Map<String, String> extractRequestHeaders(ContentCachingRequestWrapper requestWrapper) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value;
            if ("authorization".equalsIgnoreCase(name) || "cookie".equalsIgnoreCase(name)) {
                value = "<redacted>";
            } else {
                value = requestWrapper.getHeader(name);
            }
            headers.put(name, value);
        }
        return headers;
    }

    private Map<String, String> extractResponseHeaders(ContentCachingResponseWrapper responseWrapper) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : responseWrapper.getHeaderNames()) {
            String value;
            if ("set-cookie".equalsIgnoreCase(name)) {
                value = "<redacted>";
            } else {
                value = responseWrapper.getHeader(name);
            }
            headers.put(name, value);
        }
        return headers;
    }

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
}
