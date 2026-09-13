package com.marcosperboni.orderservice.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, UUID customerId, BigDecimal totalAmount) {
}
