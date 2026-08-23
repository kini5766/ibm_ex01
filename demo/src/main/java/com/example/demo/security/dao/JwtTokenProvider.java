package com.example.demo.security.dao;

import com.example.demo.security.config.AuthProperties;
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

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final AuthProperties authProperties;
    private SecretKey accessKey;

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(authProperties.accessTokenSecret()));
    }


    public String createToken(String sub) {
        Date now = new Date();
        return Jwts.builder()
                .subject(sub)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + authProperties.accessTokenExpiration().toMillis()))
                .signWith(accessKey)
                .compact();
    }

    // 토큰에서 Claims 추출
    public String parseClaimSub(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(accessKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
