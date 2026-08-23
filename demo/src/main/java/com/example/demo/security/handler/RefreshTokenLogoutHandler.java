package com.example.demo.security.handler;

import com.example.demo.security.service.TokenService;
import com.example.demo.security.dao.JwtTokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final TokenService tokenService;
    private final JwtTokenResolver jwtTokenResolver;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        // TODO
    }
}
