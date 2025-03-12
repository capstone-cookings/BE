package com.cook.cookapp.common;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String jwtSchemeName = "jwtAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.APIKEY) // APIKEY 방식으로 설정
                                .in(SecurityScheme.In.HEADER) // 헤더에서 인증 값 읽기
                                .description("Enter your JWT token without 'Bearer ' prefix")))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .addServersItem(new Server().url("/").description("https 설정"))
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0.0")
                        .description("API Documentation without Bearer prefix in Authorization header"));
    }
}
