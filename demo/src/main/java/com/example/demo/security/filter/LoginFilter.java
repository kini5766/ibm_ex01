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

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    public static final String LOGIN_FILTER_URL = "/api/login";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager) {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, LOGIN_FILTER_URL),
                authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType()) &&
                !request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            logger.error("Content-Type must be application/json");
            throw new AuthenticationServiceException("Content-Type must be application/json");
        }

        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

        String username = credentials.getOrDefault(SPRING_SECURITY_FORM_USERNAME_KEY, "").trim();
        String password = credentials.getOrDefault(SPRING_SECURITY_FORM_PASSWORD_KEY, "").trim();

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}