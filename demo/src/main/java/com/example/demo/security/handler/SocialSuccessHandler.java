package com.example.demo.security.handler;

import com.example.demo.domain.user.entity.UserRoleType;
import com.example.demo.security.jwt.dto.JWTResponseDTO;
import com.example.demo.security.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialSuccessHandler implements AuthenticationSuccessHandler {
    public static final String REDIRECT_LOCATION = "http://localhost:5173/cookie";

    @Value("${access-token-expires-in}")
    private long accessTokenExpiresIn;

    @Value("${refresh-token-expires-in}")
    private long refreshTokenExpiresIn;

    public static final String COOKIE_NAME_ACCESS_TOKEN = "access_token";
    public static final String COOKIE_NAME_REFRESH_TOKEN = "refresh_token";

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        List<String> roles = List.of("ROLE_" + UserRoleType.USER.name());

        JWTResponseDTO tokens = jwtService.issueTokens(username, roles);

        ResponseCookie accessCookie = ResponseCookie.from(COOKIE_NAME_ACCESS_TOKEN, tokens.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(accessTokenExpiresIn))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(COOKIE_NAME_REFRESH_TOKEN, tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(refreshTokenExpiresIn))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(REDIRECT_LOCATION);
    }
}