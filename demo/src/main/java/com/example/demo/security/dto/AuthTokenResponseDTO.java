package com.example.demo.security.dto;

public record AuthTokenResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static AuthTokenResponseDTO of(String accessToken, String refreshToken) {
        return new AuthTokenResponseDTO(accessToken, refreshToken, "Bearer");
    }
}
