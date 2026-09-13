package com.marcosperboni.angularbff.controller;

import com.marcosperboni.angularbff.client.CustomerServiceClient;
import com.marcosperboni.angularbff.client.OrderServiceClient;
import com.marcosperboni.angularbff.client.ProductServiceClient;
import com.marcosperboni.angularbff.dto.CreateOrderRequest;
import com.marcosperboni.angularbff.dto.OrderDetailDto;
import com.marcosperboni.angularbff.dto.OrderDetailItemDto;
import com.marcosperboni.angularbff.dto.OrderDto;
import com.marcosperboni.angularbff.dto.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bff")
public class OrderBffController {

    private final CustomerServiceClient customerServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;

    public OrderBffController(CustomerServiceClient customerServiceClient, ProductServiceClient productServiceClient,
            OrderServiceClient orderServiceClient) {
        this.customerServiceClient = customerServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderServiceClient = orderServiceClient;
    }

    @GetMapping("/orders/{id}/detail")
    public Mono<OrderDetailDto> orderDetail(@PathVariable String id) {
        return orderServiceClient.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")))
                .flatMap(order -> Mono.zip(
                        customerServiceClient.findById(order.customerId()),
                        productServiceClient.findAll().collectList())
                        .map(tuple -> {
                            Map<String, String> productNamesById = tuple.getT2().stream()
                                    .collect(Collectors.toMap(ProductDto::id, ProductDto::name, (a, b) -> a));
                            List<OrderDetailItemDto> items = order.items().stream()
                                    .map(item -> new OrderDetailItemDto(
                                            productNamesById.getOrDefault(item.productId(), "Unknown product"),
                                            item.quantity(),
                                            item.unitPrice()))
                                    .collect(Collectors.toList());
                            return new OrderDetailDto(order.id(), tuple.getT1().name(), items, order.totalAmount(),
                                    order.status());
                        }));
    }

    @PostMapping("/orders")
    public Mono<ResponseEntity<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Mono<Void> customerExists = customerServiceClient.findById(request.customerId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown customer")))
                .then();

        Mono<Void> productsExist = Flux.fromIterable(request.items())
                .flatMap(item -> productServiceClient.findById(item.productId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Unknown product: " + item.productId()))))
                .then();

        return customerExists.then(productsExist)
                .then(orderServiceClient.create(request))
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order));
    }
}
