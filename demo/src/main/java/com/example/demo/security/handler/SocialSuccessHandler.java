package com.example.demo.security.handler;

import com.example.demo.domain.user.entity.UserRoleType;
import com.example.demo.security.auth.dto.JWTResponseDTO;
import com.example.demo.security.auth.service.JwtService;
import com.example.demo.security.jwt.JwtProperties;
import com.example.demo.security.oauth.CustomOAuth2User;
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
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialSuccessHandler implements AuthenticationSuccessHandler {
    public static final String REDIRECT_LOCATION = "http://localhost:5173/cookie";

    public static final String COOKIE_NAME_ACCESS_TOKEN = "access_token";
    public static final String COOKIE_NAME_REFRESH_TOKEN = "refresh_token";

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        List<String> roles = List.of(CustomOAuth2User.toName(UserRoleType.USER));

        JWTResponseDTO tokens = jwtService.issueTokens(username, roles);

        ResponseCookie accessCookie = ResponseCookie.from(COOKIE_NAME_ACCESS_TOKEN, tokens.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.accessTokenExpiration()))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(COOKIE_NAME_REFRESH_TOKEN, tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        response.sendRedirect(REDIRECT_LOCATION);
    }
}