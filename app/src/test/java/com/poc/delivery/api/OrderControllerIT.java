package com.poc.delivery.api;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Disabled("Desativado temporariamente até termos configuração estável de integração end-to-end")
class OrderControllerIT {

    private static final String DEFAULT_DELIVERY_STRING = "delivery";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName(DEFAULT_DELIVERY_STRING)
        .withUsername(DEFAULT_DELIVERY_STRING)
        .withPassword(DEFAULT_DELIVERY_STRING);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        UUID clienteId = UUID.randomUUID();
        UUID lojaId = UUID.randomUUID();
        UUID enderecoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        String payload = "{" +
            "\"clienteId\":\"" + clienteId + "\"," +
            "\"lojaId\":\"" + lojaId + "\"," +
            "\"enderecoId\":\"" + enderecoId + "\"," +
            "\"itens\":[{" +
            "\"produtoId\":\"" + produtoId + "\"," +
            "\"quantidade\":2," +
            "\"precoUnitario\":10.0," +
            "\"observacoes\":null" +
            "}]" +
            "}";

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.clienteId").value(clienteId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lojaId").value(lojaId.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.enderecoId").value(enderecoId.toString()))
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Assertions.assertThat(responseBody).isNotBlank();
    }
}
