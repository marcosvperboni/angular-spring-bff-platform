package com.marcosperboni.angularbff.client;

import com.marcosperboni.angularbff.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClient(WebClient.Builder builder,
            @Value("${services.customer.url:http://localhost:8081}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Flux<CustomerDto> findAll() {
        return webClient.get().uri("/api/customers").retrieve().bodyToFlux(CustomerDto.class);
    }

    public Mono<CustomerDto> findById(String id) {
        return webClient.get().uri("/api/customers/{id}", id).retrieve().bodyToMono(CustomerDto.class);
    }

    public Mono<CustomerDto> create(CustomerDto request) {
        return webClient.post().uri("/api/customers").bodyValue(request).retrieve().bodyToMono(CustomerDto.class);
    }

    public Mono<CustomerDto> update(String id, CustomerDto request) {
        return webClient.put().uri("/api/customers/{id}", id).bodyValue(request).retrieve()
                .bodyToMono(CustomerDto.class);
    }

    public Mono<Void> delete(String id) {
        return webClient.delete().uri("/api/customers/{id}", id).retrieve().bodyToMono(Void.class);
    }
}
