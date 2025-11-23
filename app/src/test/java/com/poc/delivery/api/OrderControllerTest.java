package com.poc.delivery.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.domain.model.StatusPedido;
import com.poc.delivery.domain.usecase.CreateOrderUseCase;

class OrderControllerTest {

    private MockMvc mockMvc;
    private CreateOrderUseCase useCase;
    private ObjectMapper objectMapper;
    private static final String ORDERS_URL = "/api/v1/orders";
    private static final String ERROR_CODE_PATH = "$.error.code";
    private static final String ERROR_MESSAGE_PATH = "$.error.message";
    private static final String BAD_REQUEST_CODE = "BAD_REQUEST";

    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(CreateOrderUseCase.class);
        OrderController controller = new OrderController(useCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void deveDelegarParaUseCaseERetornar201() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        PedidoResponse response = new PedidoResponse(
            pedidoId,
            clienteId,
            lojaId,
            enderecoId,
            List.of(),
            StatusPedido.CRIADO,
            BigDecimal.valueOf(20),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            null
        );

        Mockito.when(useCase.execute(Mockito.any(PedidoRequest.class))).thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(pedidoId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clienteId").value(clienteId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lojaId").value(lojaId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.enderecoId").value(enderecoId.toString()));

        Mockito.verify(useCase).execute(Mockito.any(PedidoRequest.class));
    }

    @Test
    void deveRetornar500QuandoUseCaseLancarExcecaoInesperada() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        Mockito.when(useCase.execute(Mockito.any(PedidoRequest.class)))
            .thenThrow(new RuntimeException("erro inesperado"));

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value("INTERNAL_ERROR"))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("An unexpected error occurred"));

        Mockito.verify(useCase).execute(Mockito.any(PedidoRequest.class));
    }

    @Test
    void deveRetornar400QuandoUseCaseLancarIllegalArgumentException() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        IllegalArgumentException exception = new IllegalArgumentException("pedidoInvalido");

        Mockito.when(useCase.execute(Mockito.any(PedidoRequest.class)))
            .thenThrow(exception);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("pedidoInvalido"));

        Mockito.verify(useCase).execute(Mockito.any(PedidoRequest.class));
    }

    @Test
    void deveRetornar400QuandoClienteIdForNulo() throws Exception {
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            null,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("clienteId é obrigatório"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoLojaIdForNulo() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            null,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("lojaId é obrigatório"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoEnderecoIdForNulo() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            null,
            List.of(new ItemPedidoRequest(
                produtoId,
                2,
                BigDecimal.TEN,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("enderecoId é obrigatório"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoItensForemVazios() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of()
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("itens não pode ser vazio"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoQuantidadeForMenorQueUm() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                0,
                BigDecimal.TEN,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("quantidade deve ser maior que zero"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoProdutoIdForNulo() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                null,
                1,
                BigDecimal.TEN,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("produtoId é obrigatório"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoPrecoUnitarioForNulo() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                1,
                null,
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("precoUnitario é obrigatório"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoPrecoUnitarioForNegativo() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                1,
                BigDecimal.valueOf(-1),
                null
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH)
                .value("precoUnitario deve ser maior ou igual a zero"));

        Mockito.verifyNoInteractions(useCase);
    }

    @Test
    void deveRetornar400QuandoObservacoesUltrapassarLimite() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        String observacoes = "a".repeat(501);

        PedidoRequest request = new PedidoRequest(
            clienteId,
            lojaId,
            enderecoId,
            List.of(new ItemPedidoRequest(
                produtoId,
                1,
                BigDecimal.TEN,
                observacoes
            ))
        );

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH)
                .value("observacoes deve ter no máximo 500 caracteres"));

        Mockito.verifyNoInteractions(useCase);
    }
}
