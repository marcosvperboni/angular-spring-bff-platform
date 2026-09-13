package com.marcosperboni.orderservice.controller;

import com.marcosperboni.orderservice.dto.CreateOrderRequest;
import com.marcosperboni.orderservice.dto.OrderDto;
import com.marcosperboni.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public List<OrderDto> findAll() {
		return orderService.findAll();
	}

	@GetMapping("/{id}")
	public OrderDto findById(@PathVariable UUID id) {
		return orderService.findById(id);
	}

	@GetMapping("/customer/{customerId}")
	public List<OrderDto> findByCustomerId(@PathVariable UUID customerId) {
		return orderService.findByCustomerId(customerId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderDto create(@Valid @RequestBody CreateOrderRequest request) {
		return orderService.create(request);
	}
}
