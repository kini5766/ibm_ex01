package com.example.demo.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Frontend frontend,
        Cookie cookie
) {
    public record Frontend(
            String url
    ) {}

    public record Cookie(
            boolean secure,
            String sameSite,
            String accessTokenName,
            String refreshTokenName
    ) {}
}