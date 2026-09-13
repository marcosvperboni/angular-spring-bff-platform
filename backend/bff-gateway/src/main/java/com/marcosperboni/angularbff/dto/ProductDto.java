package com.marcosperboni.angularbff.dto;

import java.math.BigDecimal;

public record ProductDto(String id, String name, String description, BigDecimal price, Integer stockQuantity,
        String category) {
}
