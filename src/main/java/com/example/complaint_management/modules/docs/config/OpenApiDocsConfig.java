package com.example.complaint_management.modules.docs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiDocsConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI complaintManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Complaint Management API")
                        .description("APIs for authentication and complaint lifecycle management")
                        .version("v1")
                        .contact(new Contact().name("Complaint Management Team").email("support@example.com"))
                        .license(new License().name("Proprietary")))
                    .components(new Components().addSecuritySchemes(
                        BEARER_SCHEME,
                        new SecurityScheme()
                            .name(BEARER_SCHEME)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    ));
    }
}
