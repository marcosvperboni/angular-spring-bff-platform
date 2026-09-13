package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;

public record OrderDetailItemDto(String productName, Integer quantity, BigDecimal unitPrice) {
}
