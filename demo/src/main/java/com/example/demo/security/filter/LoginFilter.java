package com.example.demo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.util.Map;

/**
 * POST /api/login 으로 JSON 형태의 username/password를 받아서 인증하는 필터
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    public static final String LOGIN_FILTER_URL = "/api/login";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager) {
        // 이 URL로 들어오는 요청만 이 필터가 처리
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, LOGIN_FILTER_URL),
                authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        // Content-Type이 JSON이 아니면 예외
        if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType()) &&
                !request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            logger.error("Content-Type must be application/json");
            throw new AuthenticationServiceException("Content-Type must be application/json");
        }

        // JSON 본문 파싱
        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

        String username = credentials.getOrDefault(SPRING_SECURITY_FORM_USERNAME_KEY, "").trim();
        String password = credentials.getOrDefault(SPRING_SECURITY_FORM_PASSWORD_KEY, "").trim();

        // 인증 토큰 생성 (아직 인증되지 않은 상태)
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        setDetails(request, authRequest);

        // AuthenticationManager에게 인증 위임
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}