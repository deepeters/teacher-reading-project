package com.njenga.teacher_reading_portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI teacherReadingPortalOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Teacher Reading Assignment Portal API")
                        .description("REST API for assigning books and tracking student reading progress.")
                        .version("v1"));
    }
}
