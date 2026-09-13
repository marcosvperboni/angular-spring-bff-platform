package com.marcosperboni.paymentservice.service;

import com.marcosperboni.paymentservice.dto.PaymentDto;
import com.marcosperboni.paymentservice.entity.Payment;

final class PaymentMapper {

	private PaymentMapper() {
	}

	static PaymentDto toDto(Payment payment) {
		return new PaymentDto(
				payment.getId(),
				payment.getOrderId(),
				payment.getAmount(),
				payment.getStatus(),
				payment.getMethod(),
				payment.getCreatedAt());
	}
}
