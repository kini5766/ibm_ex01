package com.example.demo.security.handler;

import com.example.demo.security.jwt.service.JwtService;
import com.example.demo.security.jwt.util.JwtTokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final JwtTokenResolver jwtTokenResolver;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {

        String refreshToken = jwtTokenResolver.resolveBearerToken(request);
        if (refreshToken == null) {
            return;
        }

        if (StringUtils.hasText(refreshToken)) {
            jwtService.removeRefresh(refreshToken);
        }
    }
}
