package com.example.demo.security.jwt.repository;

import com.example.demo.security.jwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshEntity, Long> {

    Optional<RefreshEntity> findByRefreshHashAndRevokedFalse(String token);

    @Modifying
    @Query("DELETE FROM RefreshEntity r WHERE r.username = :username AND r.revoked = false")
    void deleteByUsernameAndRevokedFalse(@Param("username") String username);

    void deleteByCreatedDateBefore(LocalDateTime createdDate);
}