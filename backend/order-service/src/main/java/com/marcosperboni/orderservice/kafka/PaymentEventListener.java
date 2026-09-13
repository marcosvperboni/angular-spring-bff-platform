package com.marcosperboni.orderservice.kafka;

import com.marcosperboni.orderservice.events.PaymentProcessedEvent;
import com.marcosperboni.orderservice.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

	private final OrderService orderService;

	public PaymentEventListener(OrderService orderService) {
		this.orderService = orderService;
	}

	@KafkaListener(topics = "payment-events", groupId = "order-service")
	public void onPaymentProcessed(PaymentProcessedEvent event) {
		orderService.applyPaymentResult(event);
	}
}
