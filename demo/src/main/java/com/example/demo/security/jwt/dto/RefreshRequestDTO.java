package com.example.demo.security.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(
        @NotBlank String refreshToken
) {
}