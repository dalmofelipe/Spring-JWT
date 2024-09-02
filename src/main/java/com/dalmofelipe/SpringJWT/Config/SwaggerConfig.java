package com.dalmofelipe.SpringJWT.Config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Título da API")
                .version("1.0")
                .description("Descrição da API")
                .contact(new Contact()
                    .name("Dalmo Felipe")
                    .url("http://seusite.com")
                    .email("seuemail@dominio.com")))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Token"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Token", new SecurityScheme()
                    .type(Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
