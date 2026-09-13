package com.marcosperboni.angularbff.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Spring Boot 4's Jackson autoconfiguration wires the new Jackson 3 (tools.jackson) ObjectMapper,
     * not this classic com.fasterxml.jackson one that jjwt-jackson and the Redis cache-aside code need.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
