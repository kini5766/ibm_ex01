package com.example.demo.security.auth.repository;

import com.example.demo.security.auth.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshEntity, Long> {

    Optional<RefreshEntity> findByRefreshHashAndRevokedFalse(String tokenHash);

    void deleteByUsernameAndRevokedFalse(String username);

    void deleteByCreatedDateBefore(LocalDateTime cutoff);
}