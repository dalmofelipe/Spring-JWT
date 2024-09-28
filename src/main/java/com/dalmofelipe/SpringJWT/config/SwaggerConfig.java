package com.dalmofelipe.SpringJWT.config;

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
            .info(new Info().title("Spring JWT").version("0.1.0")
                .description("Teste Spring Boot 3 & Spring Security 6")
                .contact(new Contact().name("Dalmo Felipe").url("http://github.com/dalmofelipe")
                    .email("dalmofelipe.dev@gmail.com")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Token", new SecurityScheme().type(Type.HTTP)
                    .scheme("bearer").bearerFormat("JWT")));
    }
}
