package com.example.demo.security.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(
        @NotBlank String refreshToken
) {
}