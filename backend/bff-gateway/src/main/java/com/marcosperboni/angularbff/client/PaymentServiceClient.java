package com.marcosperboni.angularbff.client;

import com.marcosperboni.angularbff.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class PaymentServiceClient {

    private final WebClient webClient;

    public PaymentServiceClient(WebClient.Builder builder,
            @Value("${services.payment.url:http://localhost:8084}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Flux<PaymentDto> findAll() {
        return webClient.get().uri("/api/payments").retrieve().bodyToFlux(PaymentDto.class);
    }
}
