package com.example.demo.security.jwt.util;

import com.example.demo.security.jwt.entity.JwtClaims;
import com.example.demo.security.jwt.entity.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey ACCESS_KEY;
    private SecretKey REFRESH_KEY;

    @PostConstruct
    public void init() {
        ACCESS_KEY = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.accessTokenSecret()));
        REFRESH_KEY = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.refreshTokenSecret()));
    }

    public String createAccessToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.ACCESS, jwtProperties.accessTokenExpiration(), ACCESS_KEY);
    }

    public String createRefreshToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.REFRESH, jwtProperties.refreshTokenExpiration(), REFRESH_KEY);
    }

    private String createToken(String sub, List<String> roles, TokenType type, long validity, SecretKey key) {
        Date now = new Date();
        return Jwts.builder()
                .subject(sub)
                .claim("roles", roles)
                .claim("type", type.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validity))
                .signWith(key)
                .compact();
    }

    // 토큰에서 Claims 추출
    public JwtClaims parseClaims(String token, TokenType tokenType) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey(tokenType))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String sub = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        TokenType type = TokenType.valueOf(claims.get("type", String.class));
        return new JwtClaims(sub, roles, type);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, TokenType type) {
        try {
            Jwts.parser()
                    .verifyWith(getKey(type))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getKey(TokenType type) {
        return switch (type) {
            case ACCESS -> ACCESS_KEY;
            case REFRESH -> REFRESH_KEY;
        };
    }
}
