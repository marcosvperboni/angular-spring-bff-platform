package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(long totalCustomers, long totalProducts, long totalOrders, BigDecimal totalRevenue,
        List<RecentOrderDto> recentOrders) {
}
