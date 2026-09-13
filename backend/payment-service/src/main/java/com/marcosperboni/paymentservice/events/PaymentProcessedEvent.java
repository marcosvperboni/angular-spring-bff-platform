package com.marcosperboni.paymentservice.events;

import java.util.UUID;

public record PaymentProcessedEvent(UUID orderId, UUID paymentId, String status) {
}
