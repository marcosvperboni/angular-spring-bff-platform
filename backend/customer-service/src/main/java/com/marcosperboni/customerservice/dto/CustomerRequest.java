package com.marcosperboni.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone,
        String address
) {
}
