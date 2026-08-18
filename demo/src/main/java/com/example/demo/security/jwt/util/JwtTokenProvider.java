package com.example.demo.security.jwt.util;

import com.example.demo.security.jwt.entity.JwtClaims;
import com.example.demo.security.jwt.entity.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final Long accessTokenExpiresIn;
    @Getter
    private final Long refreshTokenExpiresIn;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expires-in}") long accessTokenExpiresIn,
            @Value("${jwt.refresh-token-expires-in}") long refreshTokenExpiresIn) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public String createAccessToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.ACCESS, accessTokenExpiresIn);
    }

    public String createRefreshToken(String sub, List<String> roles) {
        return createToken(sub, roles, TokenType.REFRESH, refreshTokenExpiresIn);
    }
    private String createToken(String sub, List<String> roles, TokenType type, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .subject(sub)
                .claim("roles", roles)
                .claim("type", type.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 토큰에서 Claims 추출
    public JwtClaims parseClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String sub = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        TokenType type = TokenType.valueOf(claims.get("type", String.class));

        return new JwtClaims(sub, roles, type);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        return parseClaims(token).isAccessToken();
    }

    // Refresh Token인지 확인
    public boolean isRefreshToken(String token) {
        return parseClaims(token).isRefreshToken();
    }

}
