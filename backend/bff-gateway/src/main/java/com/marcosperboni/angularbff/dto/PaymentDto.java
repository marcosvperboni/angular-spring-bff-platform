package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;

public record PaymentDto(String id, String orderId, BigDecimal amount, String status, String method) {
}
