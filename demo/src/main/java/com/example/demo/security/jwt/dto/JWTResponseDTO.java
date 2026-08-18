package com.example.demo.security.jwt.dto;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static JWTResponseDTO of(String accessToken, String refreshToken) {
        return new JWTResponseDTO(accessToken, refreshToken, "Bearer");
    }
}
