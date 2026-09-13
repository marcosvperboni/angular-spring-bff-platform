package com.marcosperboni.angularbff.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosperboni.angularbff.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Product lookups are read far more often than products change, so single-product reads go through
 * a Redis cache-aside (30s TTL) to demonstrate the BFF offloading read pressure from the downstream service.
 */
@Component
public class ProductServiceClient {

    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductServiceClient(WebClient.Builder builder,
            ReactiveRedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${services.product.url:http://localhost:8082}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Flux<ProductDto> findAll() {
        return webClient.get().uri("/api/products").retrieve().bodyToFlux(ProductDto.class);
    }

    public Mono<ProductDto> findById(String id) {
        String cacheKey = "product:" + id;
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(this::deserialize)
                .switchIfEmpty(webClient.get().uri("/api/products/{id}", id).retrieve().bodyToMono(ProductDto.class)
                        .flatMap(product -> cache(cacheKey, product).thenReturn(product)));
    }

    private Mono<ProductDto> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, ProductDto.class));
        } catch (Exception ex) {
            return Mono.empty();
        }
    }

    private Mono<Boolean> cache(String key, ProductDto product) {
        try {
            return redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(product), CACHE_TTL);
        } catch (Exception ex) {
            return Mono.just(false);
        }
    }

    public Mono<ProductDto> create(ProductDto request) {
        return webClient.post().uri("/api/products").bodyValue(request).retrieve().bodyToMono(ProductDto.class);
    }

    public Mono<ProductDto> update(String id, ProductDto request) {
        return webClient.put().uri("/api/products/{id}", id).bodyValue(request).retrieve()
                .bodyToMono(ProductDto.class);
    }

    public Mono<Void> delete(String id) {
        return webClient.delete().uri("/api/products/{id}", id).retrieve().bodyToMono(Void.class);
    }
}
