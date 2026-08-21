package com.example.demo.security.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private static final String TOKEN_KET = "refresh:token:";
    private static final String USER_KET = "refresh:token:";

    private final StringRedisTemplate redis;

}