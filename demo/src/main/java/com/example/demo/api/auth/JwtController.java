package com.example.demo.api.auth;

import com.example.demo.common.config.AppProperties;
import com.example.demo.security.dto.AuthTokenResponseDTO;
import com.example.demo.security.dto.RefreshRequestDTO;
import com.example.demo.security.service.TokenService;
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

    public static final String EXCHANGE_URL = "/jwt/exchange";
    public static final String REFRESH_URL = "/api/auth/refresh";
    public static final String LOGOUT_URL = "/api/auth/logout";
    private final TokenService tokenService;
    private final AppProperties appProperties;

    @PostMapping(EXCHANGE_URL)
    public ResponseEntity<AuthTokenResponseDTO> exchange(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractCookie(request, appProperties.cookie().accessTokenName());
        String refreshToken = extractCookie(request, appProperties.cookie().refreshTokenName());

        if (accessToken == null || refreshToken == null) {
            throw new IllegalArgumentException("Tokens not found in cookie");
        }

        clearCookie(response, appProperties.cookie().accessTokenName());
        clearCookie(response, appProperties.cookie().refreshTokenName());

        AuthTokenResponseDTO body = AuthTokenResponseDTO.of(accessToken, refreshToken);

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
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(0)
                .sameSite(appProperties.cookie().sameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping(REFRESH_URL)
    public ResponseEntity<AuthTokenResponseDTO> jwtRefreshApi(@Validated @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(tokenService.refreshRotate(dto));
    }

    @PostMapping(LOGOUT_URL)
    public ResponseEntity<Void> logoutApi(@RequestBody RefreshRequestDTO dto) {
        tokenService.removeRefresh(dto.refreshToken());
        return ResponseEntity.ok().build();
    }
}
