package com.poc.delivery.domain.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;

class OrderValidatorTest {

    private final OrderValidator validator = new OrderValidator();

    @Test
    void deveValidarPedidoValido() {
        PedidoRequest request = pedidoRequestValido();
        validator.validate(request);
    }

    @Test
    void deveLancarExcecaoQuandoRequestNulo() {
        Executable executable = () -> validator.validate(null);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoClienteIdNulo() {
        PedidoRequest request = new PedidoRequest(
            null,
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(itemPedidoValido())
        );
        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoLojaIdNulo() {
        PedidoRequest request = new PedidoRequest(
            UUID.randomUUID(),
            null,
            UUID.randomUUID(),
            List.of(itemPedidoValido())
        );
        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoEnderecoIdNulo() {
        PedidoRequest request = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            List.of(itemPedidoValido())
        );
        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoItensNulosOuVazios() {
        PedidoRequest requestComItensNulos = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            null
        );
        PedidoRequest requestComItensVazios = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of()
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(requestComItensNulos));
        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(requestComItensVazios));
    }

    @Test
    void deveLancarExcecaoQuandoItemComProdutoIdNulo() {
        ItemPedidoRequest itemInvalido = new ItemPedidoRequest(
            null,
            1,
            BigDecimal.TEN,
            null
        );
        PedidoRequest request = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(itemInvalido)
        );
        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeMenorOuIgualZero() {
        ItemPedidoRequest itemInvalido = new ItemPedidoRequest(
            UUID.randomUUID(),
            0,
            BigDecimal.TEN,
            null
        );
        PedidoRequest request = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(itemInvalido)
        );

        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void deveLancarExcecaoQuandoPrecoUnitarioMenorOuIgualZero() {
        ItemPedidoRequest itemInvalido = new ItemPedidoRequest(
            UUID.randomUUID(),
            1,
            BigDecimal.ZERO,
            null
        );
        PedidoRequest request = new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(itemInvalido)
        );

        Executable executable = () -> validator.validate(request);
        Assertions.assertThrows(IllegalArgumentException.class, executable);
    }

    private PedidoRequest pedidoRequestValido() {
        return new PedidoRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(itemPedidoValido())
        );
    }

    private ItemPedidoRequest itemPedidoValido() {
        return new ItemPedidoRequest(
            UUID.randomUUID(),
            2,
            BigDecimal.TEN,
            null
        );
    }
}
