package com.example.demo.security.jwt.entity;

import java.util.List;

public record JwtClaims(
        String sub,
        List<String> roles,
        TokenType type
) {
}