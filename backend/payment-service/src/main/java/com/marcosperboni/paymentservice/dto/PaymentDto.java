package com.marcosperboni.paymentservice.dto;

import com.marcosperboni.paymentservice.entity.PaymentMethod;
import com.marcosperboni.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
		UUID id,
		UUID orderId,
		BigDecimal amount,
		PaymentStatus status,
		PaymentMethod method,
		Instant createdAt) {
}
