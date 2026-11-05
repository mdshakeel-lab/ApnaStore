package com.apnastore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI & Swagger UI Configuration.
 * Configures the interactive documentation at /swagger-ui.html with global
 * Bearer JWT authentication support ("Authorize" button).
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI apnaStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Apna Store — Royal Couture REST API")
                        .version("1.0.0")
                        .description("Interactive REST API documentation for the Apna Store Royal Couture platform. "
                                + "Explore public catalog endpoints, authenticate with JWT, and submit secured orders.")
                        .contact(new Contact()
                                .name("Apna Store Engineering Team")
                                .email("contact@apnastore.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                // Attach security requirement so Swagger sends Authorization header when token is supplied
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token obtained from POST /api/auth/login or POST /api/auth/register. "
                                        + "Example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}

