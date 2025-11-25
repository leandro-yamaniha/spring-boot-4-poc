package com.poc.delivery.common;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class FlywayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${spring.flyway.url}")
    private String flywayUrl;

    @Value("${spring.flyway.user}")
    private String flywayUser;

    @Value("${spring.flyway.password}")
    private String flywayPassword;

    @Bean
    public Flyway flyway() {
        LOGGER.info("Configuring Flyway for local profile");
        Flyway flyway = Flyway.configure()
            .dataSource(flywayUrl, flywayUser, flywayPassword)
            .locations("classpath:db/migration")
            .load();

        LOGGER.info("Executing Flyway migrations...");
        flyway.migrate();
        LOGGER.info("Flyway migrations completed");

        return flyway;
    }
}
