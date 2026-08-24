package com.example.demo.security.service;

import com.example.demo.security.dto.RefreshRequestDTO;
import com.example.demo.security.dto.AuthTokenResponseDTO;
import com.example.demo.security.dao.RefreshTokenDAO;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenDAO refreshTokenDAO;
    private final CustomUserDetailsService userDetailsService;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    public AuthTokenResponseDTO issueTokens(String sub) {
        UserDetails userDetails = findAndValidateUser(sub);

        String newRawRefresh = RefreshTokenDAO.generateOpaqueRefreshToken();
        String newHash = RefreshTokenDAO.hash(newRawRefresh);
        refreshTokenDAO.save(newHash, sub);

        String newAccessToken = jwtTokenProvider.createToken(sub, userDetails.getAuthorities());
        return AuthTokenResponseDTO.of(newAccessToken, newRawRefresh);
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

        String newRawRefresh = RefreshTokenDAO.generateOpaqueRefreshToken();
        String newHash = RefreshTokenDAO.hash(newRawRefresh);
        refreshTokenDAO.save(newHash, sub);

        String newAccessToken = jwtTokenProvider.createToken(sub, userDetails.getAuthorities());
        return AuthTokenResponseDTO.of(newAccessToken, newRawRefresh);
    }

    public void removeRefresh(String rawRefreshToken) {
        String tokenHash = RefreshTokenDAO.hash(rawRefreshToken);
        refreshTokenDAO.deleteTokenHash(tokenHash);
    }

    private UserDetails findAndValidateUser(String sub) {
        long userId;
        try {
            userId = Long.parseLong(sub);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("잘못된 토큰입니다.");
        }
        UserDetails userDetails = userDetailsService.loadUserById(userId);
        userDetailsChecker.check(userDetails);
        return userDetails;
    }
}
