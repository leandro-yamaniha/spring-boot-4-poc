package com.poc.delivery.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlywayConfigTest {

    @Test
    void deveExistirClasseFlywayConfig() {
        assertThat(FlywayConfig.class).isNotNull();
    }
}
