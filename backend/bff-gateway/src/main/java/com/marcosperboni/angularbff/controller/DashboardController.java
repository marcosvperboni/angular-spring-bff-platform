package com.marcosperboni.angularbff.controller;

import com.marcosperboni.angularbff.client.CustomerServiceClient;
import com.marcosperboni.angularbff.client.OrderServiceClient;
import com.marcosperboni.angularbff.client.PaymentServiceClient;
import com.marcosperboni.angularbff.client.ProductServiceClient;
import com.marcosperboni.angularbff.dto.CustomerDto;
import com.marcosperboni.angularbff.dto.DashboardResponse;
import com.marcosperboni.angularbff.dto.OrderDto;
import com.marcosperboni.angularbff.dto.PaymentDto;
import com.marcosperboni.angularbff.dto.RecentOrderDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bff")
public class DashboardController {

    private final CustomerServiceClient customerServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public DashboardController(CustomerServiceClient customerServiceClient, ProductServiceClient productServiceClient,
            OrderServiceClient orderServiceClient, PaymentServiceClient paymentServiceClient) {
        this.customerServiceClient = customerServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderServiceClient = orderServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @GetMapping("/dashboard")
    public Mono<DashboardResponse> dashboard() {
        Mono<List<CustomerDto>> customers = customerServiceClient.findAll().collectList();
        Mono<Long> productCount = productServiceClient.findAll().count();
        Mono<List<OrderDto>> orders = orderServiceClient.findAll().collectList();
        Mono<BigDecimal> revenue = paymentServiceClient.findAll()
                .filter(p -> "APPROVED".equals(p.status()))
                .map(PaymentDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Mono.zip(customers, productCount, orders, revenue)
                .map(tuple -> {
                    List<CustomerDto> customerList = tuple.getT1();
                    long products = tuple.getT2();
                    List<OrderDto> orderList = tuple.getT3();
                    BigDecimal totalRevenue = tuple.getT4();

                    Map<String, String> customerNamesById = customerList.stream()
                            .collect(Collectors.toMap(CustomerDto::id, CustomerDto::name, (a, b) -> a));

                    List<RecentOrderDto> recentOrders = orderList.stream()
                            .sorted(Comparator.comparing(OrderDto::id).reversed())
                            .limit(5)
                            .map(order -> new RecentOrderDto(
                                    order.id(),
                                    customerNamesById.getOrDefault(order.customerId(), "Unknown"),
                                    order.items() == null ? 0 : order.items().size(),
                                    order.totalAmount(),
                                    order.status()))
                            .collect(Collectors.toList());

                    return new DashboardResponse(customerList.size(), products, orderList.size(), totalRevenue,
                            recentOrders);
                });
    }
}
