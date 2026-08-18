package com.example.demo.security.filter;

import com.example.demo.security.jwt.controller.JwtController;
import com.example.demo.security.jwt.entity.JwtClaims;
import com.example.demo.security.jwt.util.JwtTokenProvider;
import com.example.demo.security.jwt.util.JwtTokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenResolver jwtTokenResolver;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // 이 경로들은 JWT 검사를 아예 하지 않음
    private static final List<String> EXCLUDE_PATHS = List.of(
            LoginFilter.LOGIN_FILTER_URL,
            JwtController.EXCHANGE_URL,
            JwtController.REFRESH_URL,
            JwtController.LOGOUT_URL
            // TODO : 새로 api 추가할 때 마다 검토
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_PATHS.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtTokenResolver.resolveBearerToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                JwtClaims claims = jwtTokenProvider.parseClaims(token);

                if (claims.isAccessToken()) {
                    setAuthentication(claims, request);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * SecurityContext에 Authentication 설정
     */
    private void setAuthentication(JwtClaims claims, HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = claims.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        claims.sub(),
                        null,
                        authorities
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}