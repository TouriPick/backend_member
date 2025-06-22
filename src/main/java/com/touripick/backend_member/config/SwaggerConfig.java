package com.touripick.backend_member.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Touripick Backend Member API")
                        .description("Touripick 회원 백엔드 API 명세서")
                        .version("v1.0.0"));
    }
} 