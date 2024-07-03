package com.mcmp.costbe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    private static final String API_NAME = "MCMP_COST_OPTIMIZER";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "MCMP_COST_OPTIMIZER API 명세서";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mcmp-cost-optimizer")
                .pathsToMatch("/api/v2/**")
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION));
    }

}
