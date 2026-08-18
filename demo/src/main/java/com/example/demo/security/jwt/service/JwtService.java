package com.example.demo.security.jwt.service;

import com.example.demo.security.jwt.dto.RefreshRequestDTO;
import com.example.demo.security.jwt.dto.JWTResponseDTO;
import com.example.demo.security.jwt.entity.RefreshEntity;
import com.example.demo.security.jwt.util.JwtTokenProvider;
import com.example.demo.security.jwt.repository.RefreshTokenRepository;
import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshEntityRepository;


    public JWTResponseDTO cookie2Header(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractCookie(request, "access_token");
        String refreshToken = extractCookie(request, "refresh_token");

        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("Tokens not found in cookie");
        }

        // (선택) 쿠키 즉시 제거 — XSS로 쿠키 탈취 여지 줄이기
        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");

        // 프론트가 이후 Authorization 헤더로 쓰도록 body로 반환
        JWTResponseDTO body = JWTResponseDTO.of(
                accessToken,
                refreshToken
        );

        return body;
    }
    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 로그인 성공 후 토큰 발급
     */
    @Transactional
    public JWTResponseDTO issueTokens(String username, List<String> roles) {
        String accessToken = jwtTokenProvider.createAccessToken(username, roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(username, roles);

        addRefresh(username, refreshToken);

        return JWTResponseDTO.of(
                accessToken,
                refreshToken
        );
    }

    /**
     * Refresh Token 재발급 (Rotate)
     * - 기존 Refresh Token 검증
     * - 새 Access + Refresh Token 발급
     * - 기존 Refresh Token 폐기
     */
    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDTO request) {
        String rawRefreshToken = request.refreshToken();

        // 1. JWT 자체 검증 (서명, 만료, type=REFRESH)
        if (!jwtTokenProvider.validateToken(rawRefreshToken) ||
                !jwtTokenProvider.isRefreshToken(rawRefreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var claims = jwtTokenProvider.parseClaims(rawRefreshToken);
        String username = claims.sub();
        List<String> roles = claims.roles();

        // 2. DB에서 해시로 조회 + 유효성 검사
        String tokenHash = RefreshEntity.hash(rawRefreshToken);

        RefreshEntity savedToken = refreshEntityRepository.findByRefreshHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found or already revoked"));

        if (savedToken.isExpired()) {
            savedToken.revoke();
            throw new IllegalArgumentException("Refresh token expired");
        }

        // 3. 기존 토큰 폐기 (Rotate의 핵심)
        savedToken.revoke();

        // 4. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(username, roles);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username, roles);

        // 5. 새 Refresh Token 저장
        addRefresh(username, newRefreshToken);

        return JWTResponseDTO.of(
                newAccessToken,
                newRefreshToken
        );
    }

    /**
     * 로그아웃 (특정 Refresh Token 폐기)
     */
    @Transactional
    public void removeRefresh(String rawRefreshToken) {
        String tokenHash = RefreshEntity.hash(rawRefreshToken);
        refreshEntityRepository.findByRefreshHashAndRevokedFalse(tokenHash)
                .ifPresent(RefreshEntity::revoke);
    }

    /**
     * 해당 유저의 모든 Refresh Token 폐기 (전체 로그아웃)
     */
    @Transactional
    public void removeRefreshUser(String username) {
        refreshEntityRepository.deleteByUsernameAndRevokedFalse(username);
    }

    private void addRefresh(String username, String rawRefreshToken) {
        RefreshEntity entity = RefreshEntity.builder()
                .refreshHash(RefreshEntity.hash(rawRefreshToken))
                .username(username)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpiresIn()))
                .build();

        refreshEntityRepository.save(entity);
    }
}
