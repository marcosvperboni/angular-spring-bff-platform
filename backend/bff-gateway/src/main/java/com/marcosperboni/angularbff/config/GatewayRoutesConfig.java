package com.marcosperboni.angularbff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
            @Value("${services.customer.url:http://localhost:8081}") String customerUrl,
            @Value("${services.product.url:http://localhost:8082}") String productUrl,
            @Value("${services.order.url:http://localhost:8083}") String orderUrl,
            @Value("${services.payment.url:http://localhost:8084}") String paymentUrl) {
        return builder.routes()
                .route("customer-service", r -> r.path("/gateway/customers/**")
                        .filters(f -> f.rewritePath("/gateway/customers(?<segment>.*)", "/api/customers${segment}"))
                        .uri(customerUrl))
                .route("product-service", r -> r.path("/gateway/products/**")
                        .filters(f -> f.rewritePath("/gateway/products(?<segment>.*)", "/api/products${segment}"))
                        .uri(productUrl))
                .route("order-service", r -> r.path("/gateway/orders/**")
                        .filters(f -> f.rewritePath("/gateway/orders(?<segment>.*)", "/api/orders${segment}"))
                        .uri(orderUrl))
                .route("payment-service", r -> r.path("/gateway/payments/**")
                        .filters(f -> f.rewritePath("/gateway/payments(?<segment>.*)", "/api/payments${segment}"))
                        .uri(paymentUrl))
                .build();
    }
}
