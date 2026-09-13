package com.marcosperboni.orderservice.dto;

import com.marcosperboni.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
		UUID id,
		UUID customerId,
		OrderStatus status,
		BigDecimal totalAmount,
		Instant createdAt,
		List<OrderItemDto> items) {
}
