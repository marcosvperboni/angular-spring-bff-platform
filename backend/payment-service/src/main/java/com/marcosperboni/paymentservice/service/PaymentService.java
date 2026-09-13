package com.marcosperboni.paymentservice.service;

import com.marcosperboni.paymentservice.dto.PaymentDto;
import com.marcosperboni.paymentservice.entity.Payment;
import com.marcosperboni.paymentservice.entity.PaymentMethod;
import com.marcosperboni.paymentservice.entity.PaymentStatus;
import com.marcosperboni.paymentservice.events.OrderCreatedEvent;
import com.marcosperboni.paymentservice.events.PaymentProcessedEvent;
import com.marcosperboni.paymentservice.exception.PaymentNotFoundException;
import com.marcosperboni.paymentservice.repository.PaymentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

	private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

	private final PaymentRepository paymentRepository;
	private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

	public PaymentService(PaymentRepository paymentRepository, KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate) {
		this.paymentRepository = paymentRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	public List<PaymentDto> findAll() {
		return paymentRepository.findAll().stream().map(PaymentMapper::toDto).toList();
	}

	public PaymentDto findById(UUID id) {
		return PaymentMapper.toDto(paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id)));
	}

	public List<PaymentDto> findByOrderId(UUID orderId) {
		return paymentRepository.findByOrderId(orderId).stream().map(PaymentMapper::toDto).toList();
	}

	@Transactional
	public PaymentDto autoApprove(OrderCreatedEvent event) {
		Payment payment = new Payment();
		payment.setOrderId(event.orderId());
		payment.setAmount(event.totalAmount());
		payment.setMethod(PaymentMethod.PIX);
		payment.setStatus(PaymentStatus.APPROVED);

		Payment saved = paymentRepository.save(payment);
		kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, new PaymentProcessedEvent(saved.getOrderId(), saved.getId(), saved.getStatus().name()));
		return PaymentMapper.toDto(saved);
	}
}
