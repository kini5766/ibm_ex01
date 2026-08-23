package com.example.demo.security.handler;

import com.example.demo.common.config.AppProperties;
import com.example.demo.security.dto.AuthTokenResponseDTO;
import com.example.demo.security.service.TokenService;
import com.example.demo.security.config.AuthProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SocialSuccessHandler implements AuthenticationSuccessHandler {
    public static final String REDIRECT_LOCATION = "/cookie";

    private final TokenService tokenService;
    private final AppProperties appProperties;
    private final AuthProperties authProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();

        AuthTokenResponseDTO tokens = tokenService.issueTokens(username);

        ResponseCookie accessCookie = ResponseCookie.from(appProperties.cookie().accessTokenName(), tokens.accessToken())
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(authProperties.accessTokenExpiration())
                .sameSite(appProperties.cookie().sameSite())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(appProperties.cookie().refreshTokenName(), tokens.refreshToken())
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(authProperties.refreshTokenExpiration())
                .sameSite(appProperties.cookie().sameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(appProperties.frontend().url() + REDIRECT_LOCATION);
    }
}