package com.example.demo.security.jwt.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        long accessTokenExpiration,
        String accessTokenSecret,
        long refreshTokenExpiration,
        String refreshTokenSecret
) {
}