package com.marcosperboni.paymentservice.controller;

import com.marcosperboni.paymentservice.dto.PaymentDto;
import com.marcosperboni.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@GetMapping
	public List<PaymentDto> findAll() {
		return paymentService.findAll();
	}

	@GetMapping("/{id}")
	public PaymentDto findById(@PathVariable UUID id) {
		return paymentService.findById(id);
	}

	@GetMapping("/order/{orderId}")
	public List<PaymentDto> findByOrderId(@PathVariable UUID orderId) {
		return paymentService.findByOrderId(orderId);
	}
}
