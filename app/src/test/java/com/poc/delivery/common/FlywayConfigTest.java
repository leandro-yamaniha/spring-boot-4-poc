package com.poc.delivery.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlywayConfigTest {

    @Test
    void deveExistirClasseFlywayConfig() {
        // Teste básico apenas para verificar que a classe existe
        // e que Flyway pode ser importado sem erros
        assertThat(FlywayConfig.class).isNotNull();
    }
}
