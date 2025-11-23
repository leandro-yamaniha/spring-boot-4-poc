package com.poc.delivery.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Backend API")
                        .version("1.0.0")
                        .description("""
                                API REST para sistema de entrega de pedidos.
                                
                                **Stack Tecnológica:**
                                - Java 25 (LTS)
                                - Spring Boot 4.0.0
                                - PostgreSQL
                                - Redis (cache)
                                
                                **Arquitetura:**
                                - Camadas (Layered) + DDD leve
                                - Princípios de Hexagonal aplicados onde fizer sentido
                                """)
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
