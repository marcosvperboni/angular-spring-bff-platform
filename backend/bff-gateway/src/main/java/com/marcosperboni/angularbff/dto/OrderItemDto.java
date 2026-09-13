package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;

public record OrderItemDto(String productId, Integer quantity, BigDecimal unitPrice) {
}
