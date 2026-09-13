package com.marcosperboni.orderservice.service;

import com.marcosperboni.orderservice.dto.CreateOrderRequest;
import com.marcosperboni.orderservice.dto.OrderDto;
import com.marcosperboni.orderservice.entity.Order;
import com.marcosperboni.orderservice.entity.OrderItem;
import com.marcosperboni.orderservice.entity.OrderStatus;
import com.marcosperboni.orderservice.events.OrderCreatedEvent;
import com.marcosperboni.orderservice.events.PaymentProcessedEvent;
import com.marcosperboni.orderservice.exception.OrderNotFoundException;
import com.marcosperboni.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	private static final String ORDER_EVENTS_TOPIC = "order-events";

	private final OrderRepository orderRepository;
	private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

	public OrderService(OrderRepository orderRepository, KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
		this.orderRepository = orderRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	public List<OrderDto> findAll() {
		return orderRepository.findAll().stream().map(OrderMapper::toDto).toList();
	}

	public OrderDto findById(UUID id) {
		return OrderMapper.toDto(getOrderOrThrow(id));
	}

	public List<OrderDto> findByCustomerId(UUID customerId) {
		return orderRepository.findByCustomerId(customerId).stream().map(OrderMapper::toDto).toList();
	}

	@Transactional
	public OrderDto create(CreateOrderRequest request) {
		Order order = new Order();
		order.setCustomerId(request.customerId());
		order.setStatus(OrderStatus.CREATED);
		request.items().forEach(itemRequest -> {
			OrderItem item = new OrderItem();
			item.setProductId(itemRequest.productId());
			item.setQuantity(itemRequest.quantity());
			item.setUnitPrice(itemRequest.unitPrice());
			order.addItem(item);
		});
		order.setTotalAmount(computeTotal(order.getItems()));

		Order saved = orderRepository.save(order);
		kafkaTemplate.send(ORDER_EVENTS_TOPIC, new OrderCreatedEvent(saved.getId(), saved.getCustomerId(), saved.getTotalAmount()));
		return OrderMapper.toDto(saved);
	}

	@Transactional
	public void applyPaymentResult(PaymentProcessedEvent event) {
		orderRepository.findById(event.orderId()).ifPresentOrElse(order -> {
			order.setStatus("APPROVED".equalsIgnoreCase(event.status()) ? OrderStatus.PAID : OrderStatus.CANCELLED);
			orderRepository.save(order);
		}, () -> log.warn("Received payment event for unknown order id {}, skipping", event.orderId()));
	}

	static BigDecimal computeTotal(List<OrderItem> items) {
		return items.stream()
				.map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private Order getOrderOrThrow(UUID id) {
		return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
	}
}
