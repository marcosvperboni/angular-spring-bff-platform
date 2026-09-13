package com.marcosperboni.paymentservice.service;

import com.marcosperboni.paymentservice.dto.PaymentDto;
import com.marcosperboni.paymentservice.entity.Payment;
import com.marcosperboni.paymentservice.entity.PaymentMethod;
import com.marcosperboni.paymentservice.entity.PaymentStatus;
import com.marcosperboni.paymentservice.events.OrderCreatedEvent;
import com.marcosperboni.paymentservice.events.PaymentProcessedEvent;
import com.marcosperboni.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

	@Test
	void autoApprove_savesApprovedPixPaymentAndPublishesEvent() {
		PaymentService paymentService = new PaymentService(paymentRepository, kafkaTemplate);
		UUID orderId = UUID.randomUUID();
		OrderCreatedEvent event = new OrderCreatedEvent(orderId, UUID.randomUUID(), new BigDecimal("42.00"));

		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
			Payment payment = invocation.getArgument(0);
			payment.setId(UUID.randomUUID());
			return payment;
		});

		PaymentDto result = paymentService.autoApprove(event);

		assertThat(result.orderId()).isEqualTo(orderId);
		assertThat(result.amount()).isEqualByComparingTo("42.00");
		assertThat(result.status()).isEqualTo(PaymentStatus.APPROVED);
		assertThat(result.method()).isEqualTo(PaymentMethod.PIX);

		ArgumentCaptor<PaymentProcessedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentProcessedEvent.class);
		verify(kafkaTemplate).send(eq("payment-events"), eventCaptor.capture());
		assertThat(eventCaptor.getValue().orderId()).isEqualTo(orderId);
		assertThat(eventCaptor.getValue().status()).isEqualTo("APPROVED");
	}
}
