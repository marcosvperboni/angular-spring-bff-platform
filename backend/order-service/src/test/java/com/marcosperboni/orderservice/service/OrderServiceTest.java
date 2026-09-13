package com.marcosperboni.orderservice.service;

import com.marcosperboni.orderservice.dto.CreateOrderItemRequest;
import com.marcosperboni.orderservice.dto.CreateOrderRequest;
import com.marcosperboni.orderservice.dto.OrderDto;
import com.marcosperboni.orderservice.entity.Order;
import com.marcosperboni.orderservice.entity.OrderStatus;
import com.marcosperboni.orderservice.events.OrderCreatedEvent;
import com.marcosperboni.orderservice.events.PaymentProcessedEvent;
import com.marcosperboni.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	@Test
	void create_computesTotalAmountAndPublishesEvent() {
		OrderService orderService = new OrderService(orderRepository, kafkaTemplate);
		UUID customerId = UUID.randomUUID();
		CreateOrderRequest request = new CreateOrderRequest(customerId, List.of(
				new CreateOrderItemRequest(UUID.randomUUID(), 2, new BigDecimal("10.00")),
				new CreateOrderItemRequest(UUID.randomUUID(), 3, new BigDecimal("5.50"))));

		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
			Order order = invocation.getArgument(0);
			order.setId(UUID.randomUUID());
			return order;
		});

		OrderDto result = orderService.create(request);

		assertThat(result.totalAmount()).isEqualByComparingTo("36.50");
		assertThat(result.status()).isEqualTo(OrderStatus.CREATED);
		assertThat(result.items()).hasSize(2);

		ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
		verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("order-events"), eventCaptor.capture());
		assertThat(eventCaptor.getValue().totalAmount()).isEqualByComparingTo("36.50");
		assertThat(eventCaptor.getValue().customerId()).isEqualTo(customerId);
	}

	@Test
	void applyPaymentResult_marksOrderPaidWhenApproved() {
		OrderService orderService = new OrderService(orderRepository, kafkaTemplate);
		Order order = new Order();
		UUID orderId = UUID.randomUUID();
		order.setId(orderId);
		order.setStatus(OrderStatus.CREATED);
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

		orderService.applyPaymentResult(new PaymentProcessedEvent(orderId, UUID.randomUUID(), "APPROVED"));

		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
	}

	@Test
	void applyPaymentResult_skipsUnknownOrder() {
		OrderService orderService = new OrderService(orderRepository, kafkaTemplate);
		UUID orderId = UUID.randomUUID();
		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		orderService.applyPaymentResult(new PaymentProcessedEvent(orderId, UUID.randomUUID(), "DECLINED"));

		verify(orderRepository, org.mockito.Mockito.never()).save(any());
	}
}
