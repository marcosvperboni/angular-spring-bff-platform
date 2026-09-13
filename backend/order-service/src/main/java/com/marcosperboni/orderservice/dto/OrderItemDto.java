package com.marcosperboni.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(UUID id, UUID productId, Integer quantity, BigDecimal unitPrice) {
}
