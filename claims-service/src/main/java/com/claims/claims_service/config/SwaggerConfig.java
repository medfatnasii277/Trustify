package com.claims.claims_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for Claims Service
 * Provides interactive API documentation with JWT authentication support
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8082}")
    private String serverPort;
    
    /**
     * Configure OpenAPI documentation
     *
     * @return the configured OpenAPI object
     */
    @Bean
    public OpenAPI claimsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trustify Claims Service API")
                        .description("REST API for managing insurance claims for life, car, and house insurance policies. " +
                                "Supports claim submission, tracking, approval/rejection workflow, and settlement.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Trustify Team")
                                .email("support@trustify.com")
                                .url("https://trustify.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT token obtained from Keycloak authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
