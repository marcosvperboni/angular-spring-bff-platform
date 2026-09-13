package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailDto(String id, String customerName, List<OrderDetailItemDto> items,
        BigDecimal totalAmount, String status) {
}
