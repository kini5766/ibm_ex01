package com.example.demo.security.dto;

public record AuthTokenResponseDTO(
        String accessToken,
        String refreshToken
) {
}
