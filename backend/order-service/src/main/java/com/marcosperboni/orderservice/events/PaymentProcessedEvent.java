package com.marcosperboni.orderservice.events;

import java.util.UUID;

public record PaymentProcessedEvent(UUID orderId, UUID paymentId, String status) {
}
