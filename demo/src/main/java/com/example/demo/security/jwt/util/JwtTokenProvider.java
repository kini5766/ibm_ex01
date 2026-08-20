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
    private SecretKey accessKey;
    private SecretKey refreshKey;

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.accessTokenSecret()));
        refreshKey = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.refreshTokenSecret()));
    }

    public String createAccessToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.ACCESS, jwtProperties.accessTokenExpiration());
    }

    public String createRefreshToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.REFRESH, jwtProperties.refreshTokenExpiration());
    }

    private String createToken(String sub, List<String> roles, TokenType type, long validity) {
        Date now = new Date();
        return Jwts.builder()
                .subject(sub)
                .claim(JwtClaims.ROLES, roles)
                .claim(JwtClaims.TYPE, type.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validity))
                .signWith(getKey(type))
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
        List<?> rawRoles = claims.get(JwtClaims.ROLES, List.class);
        List<String> roles = rawRoles == null ? List.of() :
                rawRoles.stream().map(String::valueOf).toList();
        TokenType type = TokenType.valueOf(claims.get(JwtClaims.TYPE, String.class));
        return new JwtClaims(sub, roles, type);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, TokenType tokenType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey(tokenType))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            TokenType type = TokenType.valueOf(claims.get(JwtClaims.TYPE, String.class));
            return type == tokenType;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getKey(TokenType type) {
        return switch (type) {
            case ACCESS -> accessKey;
            case REFRESH -> refreshKey;
        };
    }
}
