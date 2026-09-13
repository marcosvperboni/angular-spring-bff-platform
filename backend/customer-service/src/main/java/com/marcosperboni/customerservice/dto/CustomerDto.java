package com.marcosperboni.customerservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerDto(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        Instant createdAt
) {
}
