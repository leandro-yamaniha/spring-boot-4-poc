package com.poc.delivery.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.poc.delivery.api.dto.ItemPedidoRequest;
import com.poc.delivery.api.dto.PedidoRequest;
import com.poc.delivery.api.dto.PedidoResponse;
import com.poc.delivery.domain.model.StatusPedido;
import com.poc.delivery.domain.usecase.CreateOrderUseCase;
import com.poc.delivery.domain.usecase.GetOrderUseCase;
import com.poc.delivery.domain.usecase.OrderNotFoundException;

class OrderControllerTest {

    private MockMvc mockMvc;
    private CreateOrderUseCase createOrderUseCase;
    private GetOrderUseCase getOrderUseCase;
    private ObjectMapper objectMapper;
    private static final String ORDERS_URL = "/api/v1/orders";
    private static final String ERROR_CODE_PATH = "$.error.code";
    private static final String ERROR_MESSAGE_PATH = "$.error.message";
    private static final String BAD_REQUEST_CODE = "BAD_REQUEST";

    @BeforeEach
    void setUp() {
        createOrderUseCase = Mockito.mock(CreateOrderUseCase.class);
        getOrderUseCase = Mockito.mock(GetOrderUseCase.class);
        OrderController controller = new OrderController(createOrderUseCase, getOrderUseCase);
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

        Mockito.when(createOrderUseCase.execute(Mockito.any(PedidoRequest.class))).thenReturn(response);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(pedidoId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clienteId").value(clienteId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lojaId").value(lojaId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.enderecoId").value(enderecoId.toString()));

        Mockito.verify(createOrderUseCase).execute(Mockito.any(PedidoRequest.class));
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

        Mockito.when(createOrderUseCase.execute(Mockito.any(PedidoRequest.class)))
            .thenThrow(new RuntimeException("erro inesperado"));

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value("INTERNAL_ERROR"))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("An unexpected error occurred"));

        Mockito.verify(createOrderUseCase).execute(Mockito.any(PedidoRequest.class));
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

        Mockito.when(createOrderUseCase.execute(Mockito.any(PedidoRequest.class)))
            .thenThrow(exception);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post(ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("pedidoInvalido"));

        Mockito.verify(createOrderUseCase).execute(Mockito.any(PedidoRequest.class));
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
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

        Mockito.verifyNoInteractions(createOrderUseCase);
    }

    @Test
    void deveRetornarPedidoQuandoIdExistir() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();

        PedidoResponse response = new PedidoResponse(
            pedidoId,
            clienteId,
            lojaId,
            enderecoId,
            List.of(),
            StatusPedido.CRIADO,
            BigDecimal.TEN,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            null,
            null
        );

        Mockito.when(getOrderUseCase.execute(pedidoId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_URL + "/{id}", pedidoId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(pedidoId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.clienteId").value(clienteId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lojaId").value(lojaId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.enderecoId").value(enderecoId.toString()));

        Mockito.verify(getOrderUseCase).execute(pedidoId);
        Mockito.verifyNoInteractions(createOrderUseCase);
    }

    @Test
    void deveRetornar404QuandoPedidoNaoExistir() throws Exception {
        UUID pedidoId = UUID.randomUUID();

        Mockito.when(getOrderUseCase.execute(pedidoId))
            .thenThrow(new OrderNotFoundException("Pedido não encontrado: " + pedidoId));

        mockMvc.perform(MockMvcRequestBuilders.get(ORDERS_URL + "/{id}", pedidoId))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_CODE_PATH).value("ORDER_NOT_FOUND"))
            .andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_PATH).value("Pedido não encontrado: " + pedidoId));

        Mockito.verify(getOrderUseCase).execute(pedidoId);
        Mockito.verifyNoInteractions(createOrderUseCase);
    }
}
