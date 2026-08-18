package com.example.demo.security.config;

import com.example.demo.security.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 매일 새벽 3시에 오래된 Refresh Token 삭제
     * - 예: 30일 이전에 생성된 토큰 삭제
     */
    @Scheduled(cron = "0 0 3 * * *")  // 초 분 시 일 월 요일
    @Transactional
    public void deleteExpiredRefreshTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);

        refreshTokenRepository.deleteByCreatedDateBefore(cutoff);

        log.info("Deleted refresh tokens created before {}", cutoff);
    }
}
