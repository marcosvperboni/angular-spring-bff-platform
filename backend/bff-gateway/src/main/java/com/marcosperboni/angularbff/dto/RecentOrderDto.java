package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;

public record RecentOrderDto(String id, String customerName, int itemCount, BigDecimal totalAmount, String status) {
}
