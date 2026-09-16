package com.example.demo.security.service;

import com.example.demo.common.config.AppProperties;
import com.example.demo.security.dto.RefreshRequestDTO;
import com.example.demo.security.dto.AuthTokenResponseDTO;
import com.example.demo.security.dao.RefreshTokenDAO;
import com.example.demo.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenDAO refreshTokenDAO;
    private final CustomUserDetailsService userDetailsService;
    private final AppProperties appProperties;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    public void removeRefresh(String rawRefreshToken) {
        String tokenHash = RefreshTokenDAO.hash(rawRefreshToken);
        refreshTokenDAO.deleteTokenHash(tokenHash);
    }

    public AuthTokenResponseDTO refreshRotate(RefreshRequestDTO request) {
        String requestRawRefresh = request.refreshToken();

        String oldHash = RefreshTokenDAO.hash(requestRawRefresh);

        Optional<String> optionalSub = refreshTokenDAO.findAndDelete(oldHash);
        if (optionalSub.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String sub = optionalSub.get();

        UserDetails userDetails = findAndValidateUser(sub);

        String newRawRefresh = refreshTokenDAO.generateAndSave(sub);
        String newAccessToken = jwtTokenProvider.createToken(sub, userDetails.getAuthorities());
        return new AuthTokenResponseDTO(newAccessToken, newRawRefresh);
    }

    public AuthTokenResponseDTO issueTokens(String sub) {
        UserDetails userDetails = findAndValidateUser(sub);

        String newRawRefresh = refreshTokenDAO.generateAndSave(sub);
        String newAccessToken = jwtTokenProvider.createToken(sub, userDetails.getAuthorities());
        return new AuthTokenResponseDTO(newAccessToken, newRawRefresh);
    }

    public AuthTokenResponseDTO cookie2Header(Cookie[] cookies) {
        if (cookies == null) {
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }
        String refreshToken = extractCookie(cookies, appProperties.cookie().refreshTokenName());
        if (refreshToken == null) {
            throw new RuntimeException("refreshToken 쿠키가 존재하지 않습니다.");
        }
        return refreshRotate(new RefreshRequestDTO(refreshToken));
    }

    public ResponseCookie emptyCookieRefreshToken() {
        return emptyCookie(appProperties.cookie().refreshTokenName());
    }

    private ResponseCookie emptyCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(0)
                .sameSite(appProperties.cookie().sameSite())
                .build();
    }

    private String extractCookie(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private UserDetails findAndValidateUser(String sub) {
        long userId;
        try {
            userId = Long.parseLong(sub);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("아이디 혹은 비밀번호가 틀렸습니다.");
        }
        UserDetails userDetails = userDetailsService.loadUserById(userId);
        userDetailsChecker.check(userDetails);
        return userDetails;
    }
}
