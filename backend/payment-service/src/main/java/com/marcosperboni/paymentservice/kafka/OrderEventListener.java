package com.marcosperboni.paymentservice.kafka;

import com.marcosperboni.paymentservice.events.OrderCreatedEvent;
import com.marcosperboni.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

	private final PaymentService paymentService;

	public OrderEventListener(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@KafkaListener(topics = "order-events", groupId = "payment-service")
	public void onOrderCreated(OrderCreatedEvent event) {
		paymentService.autoApprove(event);
	}
}
