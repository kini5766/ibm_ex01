package com.example.demo.security.config;

import com.example.demo.security.filter.JwtAuthenticationFilter;
import com.example.demo.security.filter.LoginFilter;
import com.example.demo.security.handler.JwtLogoutSuccessHandler;
import com.example.demo.security.handler.LoginFailureHandler;
import com.example.demo.security.handler.LoginSuccessHandler;
import com.example.demo.security.handler.RefreshTokenLogoutHandler;
import com.example.demo.security.jwt.controller.JwtController;
import com.example.demo.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RefreshTokenLogoutHandler jwtLogoutHandler;
    private final JwtLogoutSuccessHandler jwtLogoutSuccessHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        // 로그인 필터
        LoginFilter loginFilter = new LoginFilter(authenticationManager);
        loginFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler());
        loginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());

        // http 설정
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                LoginFilter.LOGIN_FILTER_URL,
                                JwtController.EXCHANGE_URL,
                                JwtController.REFRESH_URL,
                                JwtController.LOGOUT_URL).permitAll()
                        // TODO : api 추가할 때 작업
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(socialSuccessHandler)
                        .failureHandler(((request, response, exception) -> {
                            exception.printStackTrace();
                        }))
                )
                .logout(logout -> logout
                        .logoutUrl(JwtController.LOGOUT_URL)
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(jwtLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}