package com.marcosperboni.orderservice.service;

import com.marcosperboni.orderservice.dto.OrderDto;
import com.marcosperboni.orderservice.dto.OrderItemDto;
import com.marcosperboni.orderservice.entity.Order;
import com.marcosperboni.orderservice.entity.OrderItem;

final class OrderMapper {

	private OrderMapper() {
	}

	static OrderDto toDto(Order order) {
		return new OrderDto(
				order.getId(),
				order.getCustomerId(),
				order.getStatus(),
				order.getTotalAmount(),
				order.getCreatedAt(),
				order.getItems().stream().map(OrderMapper::toDto).toList());
	}

	static OrderItemDto toDto(OrderItem item) {
		return new OrderItemDto(item.getId(), item.getProductId(), item.getQuantity(), item.getUnitPrice());
	}
}
