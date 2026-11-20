package org.example.springbatch001.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springBatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Batch CSV Upload API")
                        .description("Upload a CSV file and import customer records using Spring Batch")
                        .version("1.0.0"));
    }
}

