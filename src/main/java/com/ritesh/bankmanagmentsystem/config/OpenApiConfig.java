package com.ritesh.bankmanagmentsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankOpenApi() {
        return new OpenAPI().info(
            new Info()
                .title("Bank Management System API")
                .version("MVP")
                .description("Secure banking APIs with JWT auth and role-based access")
                .contact(new Contact().name("Bank Ops Team").email("ops@example.com"))
        );
    }
}

