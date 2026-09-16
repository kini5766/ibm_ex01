package com.example.demo.security.handler;

import com.example.demo.common.config.AppProperties;
import com.example.demo.security.dto.AuthTokenResponseDTO;
import com.example.demo.security.service.AuthService;
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

    private final AuthService authService;
    private final AppProperties appProperties;
    private final AuthProperties authProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // authentication.getName() => CustomOAuth2User 의 getName 과 동일한 값
        String sub = authentication.getName();

        AuthTokenResponseDTO tokens = authService.issueTokens(sub);

        ResponseCookie refreshCookie = ResponseCookie.from(appProperties.cookie().refreshTokenName(), tokens.refreshToken())
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(authProperties.refreshTokenExpiration())
                .sameSite(appProperties.cookie().sameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(appProperties.frontend().url() + REDIRECT_LOCATION);
    }
}