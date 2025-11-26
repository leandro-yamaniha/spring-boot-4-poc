package com.poc.delivery.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OrderNotFoundExceptionTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Pedido não encontrado";
        OrderNotFoundException exception = new OrderNotFoundException(mensagem);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
    }

    @Test
    void deveCriarExcecaoComMensagemECausa() {
        String mensagem = "Pedido não encontrado";
        Throwable causa = new RuntimeException("Causa original");
        OrderNotFoundException exception = new OrderNotFoundException(mensagem, causa);

        assertThat(exception.getMessage()).isEqualTo(mensagem);
        assertThat(exception.getCause()).isEqualTo(causa);
    }
}
