package com.marcosperboni.angularbff.client;

import com.marcosperboni.angularbff.dto.CreateOrderRequest;
import com.marcosperboni.angularbff.dto.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OrderServiceClient {

    private final WebClient webClient;

    public OrderServiceClient(WebClient.Builder builder,
            @Value("${services.order.url:http://localhost:8083}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Flux<OrderDto> findAll() {
        return webClient.get().uri("/api/orders").retrieve().bodyToFlux(OrderDto.class);
    }

    public Mono<OrderDto> findById(String id) {
        return webClient.get().uri("/api/orders/{id}", id).retrieve().bodyToMono(OrderDto.class);
    }

    public Mono<OrderDto> create(CreateOrderRequest request) {
        return webClient.post().uri("/api/orders").bodyValue(request).retrieve().bodyToMono(OrderDto.class);
    }
}
