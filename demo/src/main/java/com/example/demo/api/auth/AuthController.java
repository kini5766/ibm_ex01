package com.example.demo.api.auth;

import com.example.demo.security.dto.RefreshRequestDTO;
import com.example.demo.security.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    public static final String EXCHANGE_URL = "/api/auth/jwt/exchange";
    public static final String REFRESH_URL = "/api/auth/refresh";
    public static final String LOGOUT_URL = "/api/auth/logout";
    public static final String LOGIN_URL = "/api/login";

    @PostMapping(EXCHANGE_URL)
    public ResponseEntity<?> exchange(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        var result = authService.cookie2Header(cookies);
        response.addHeader(HttpHeaders.SET_COOKIE, authService.emptyCookieRefreshToken().toString());
        return ResponseEntity.ok().body(result);
    }

    @PostMapping(value = REFRESH_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> jwtRefreshApi(@Validated @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(authService.refreshRotate(dto));
    }

    @PostMapping(value = LOGOUT_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logoutApi(@Validated @RequestBody RefreshRequestDTO dto) {
        authService.removeRefresh(dto.refreshToken());
        return ResponseEntity.ok().build();
    }
}
