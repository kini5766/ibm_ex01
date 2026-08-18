package com.example.demo.security.jwt.entity;

import java.util.List;

public record JwtClaims(
        String sub,
        List<String> roles,
        TokenType type
) {
    public boolean isAccessToken() {
        return type == TokenType.ACCESS;
    }
    public boolean isRefreshToken() {
        return type == TokenType.REFRESH;
    }
}