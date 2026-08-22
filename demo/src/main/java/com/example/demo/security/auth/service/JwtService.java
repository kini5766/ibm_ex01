package com.example.demo.security.auth.service;

import com.example.demo.security.auth.dto.RefreshRequestDTO;
import com.example.demo.security.auth.dto.JWTResponseDTO;
import com.example.demo.security.auth.entity.RefreshEntity;
import com.example.demo.security.auth.entity.TokenType;
import com.example.demo.security.jwt.JwtProperties;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.security.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshEntityRepository;

    @Transactional
    public JWTResponseDTO issueTokens(String username, List<String> roles) {
        String accessToken = jwtTokenProvider.createAccessToken(username, roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(username, roles);

        addRefresh(username, refreshToken);

        return JWTResponseDTO.of(accessToken, refreshToken);
    }

    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDTO request) {
        String rawRefreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateToken(rawRefreshToken, TokenType.REFRESH)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var claims = jwtTokenProvider.parseClaims(rawRefreshToken, TokenType.REFRESH);
        String username = claims.sub();
        List<String> roles = claims.roles();

        String tokenHash = hash(rawRefreshToken);

        RefreshEntity savedToken = refreshEntityRepository.findByRefreshHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or already revoked"));

        if (savedToken.isExpired()) {
            savedToken.revoke();
            throw new IllegalArgumentException("Refresh token expired");
        }

        savedToken.revoke();

        String newAccessToken = jwtTokenProvider.createAccessToken(username, roles);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username, roles);

        addRefresh(username, newRefreshToken);

        return JWTResponseDTO.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void removeRefresh(String rawRefreshToken) {
        String tokenHash = hash(rawRefreshToken);
        refreshEntityRepository.findByRefreshHashAndRevokedFalse(tokenHash)
                .ifPresent(RefreshEntity::revoke);
    }

    @Transactional
    public void removeRefreshUser(String username) {
        refreshEntityRepository.deleteByUsernameAndRevokedFalse(username);
    }

    private void addRefresh(String username, String rawRefreshToken) {
        RefreshEntity entity = RefreshEntity.builder()
                .refreshHash(hash(rawRefreshToken))
                .username(username)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiration()))
                .build();

        refreshEntityRepository.save(entity);
    }

    public static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
