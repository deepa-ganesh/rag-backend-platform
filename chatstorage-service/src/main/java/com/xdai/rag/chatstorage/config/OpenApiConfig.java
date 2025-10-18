package com.xdai.rag.chatstorage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key")
                                        .description("Provide your API key to authorize requests")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .info(new Info()
                        .title("RAG Chat Storage Service API")
                        .description("Backend microservice for securely storing and managing RAG-based chatbot sessions and messages.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Deepa Ganesh")
                                .email("deepaganesh10@gmail.com")
                                .url("https://github.com/deepa-ganesh")
                        )
                );
    }
}
