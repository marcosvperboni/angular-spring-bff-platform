package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(String id, String customerId, String status, BigDecimal totalAmount,
        List<OrderItemDto> items) {
}
