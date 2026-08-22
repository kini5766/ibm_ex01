package com.example.demo.security.auth.entity;

import java.util.List;

public record JwtClaims(
        String sub,
        List<String> roles,
        TokenType type
) {
    public static final String ROLES = "roles";
    public static final String TYPE = "type";
}