package com.poc.delivery.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Testes unitários para GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarErro500QuandoExcecaoInesperada() {
        // Arrange
        var exception = new RuntimeException("Erro inesperado");

        // Act
        var response = handler.handleUnexpectedException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isNotNull();
        assertThat(response.getBody().error().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().error().message()).isEqualTo("An unexpected error occurred");
    }

    @Test
    void deveRetornarErro500QuandoExcecaoSemMensagem() {
        // Arrange
        var exception = new NullPointerException();

        // Act
        var response = handler.handleUnexpectedException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isNotNull();
        assertThat(response.getBody().error().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().error().message()).isEqualTo("An unexpected error occurred");
    }
}
