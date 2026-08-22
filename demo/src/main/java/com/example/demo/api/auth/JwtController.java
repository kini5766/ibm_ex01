package com.example.demo.api.auth;

import com.example.demo.security.handler.SocialSuccessHandler;
import com.example.demo.security.auth.dto.JWTResponseDTO;
import com.example.demo.security.auth.dto.RefreshRequestDTO;
import com.example.demo.security.auth.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private static final boolean APP_COOKIE_SECURE = false;
    private static final String APP_COOKIE_SAME_SITE = "Lax";
    public static final String EXCHANGE_URL = "/jwt/exchange";
    public static final String REFRESH_URL = "/api/auth/refresh";
    public static final String LOGOUT_URL = "/api/auth/logout";
    private final JwtService jwtService;

    @PostMapping(EXCHANGE_URL)
    public ResponseEntity<JWTResponseDTO> exchange(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractCookie(request, SocialSuccessHandler.COOKIE_NAME_ACCESS_TOKEN);
        String refreshToken = extractCookie(request, SocialSuccessHandler.COOKIE_NAME_REFRESH_TOKEN);

        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("Tokens not found in cookie");
        }

        clearCookie(response, SocialSuccessHandler.COOKIE_NAME_ACCESS_TOKEN);
        clearCookie(response, SocialSuccessHandler.COOKIE_NAME_REFRESH_TOKEN);

        JWTResponseDTO body = JWTResponseDTO.of(accessToken, refreshToken);

        return ResponseEntity.ok().body(body);
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
                .secure(APP_COOKIE_SECURE)
                .path("/")
                .maxAge(0)
                .sameSite(APP_COOKIE_SAME_SITE)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping(REFRESH_URL)
    public ResponseEntity<JWTResponseDTO> jwtRefreshApi(@Validated @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(jwtService.refreshRotate(dto));
    }

    @PostMapping(LOGOUT_URL)
    public ResponseEntity<Void> logoutApi(@RequestBody RefreshRequestDTO dto) {
        jwtService.removeRefresh(dto.refreshToken());
        return ResponseEntity.ok().build();
    }
}
