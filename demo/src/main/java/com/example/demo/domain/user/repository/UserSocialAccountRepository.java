package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.entity.UserSocialAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccountEntity, String> {
    Boolean existsByEmail(String email);
    List<UserSocialAccountEntity> findByMemberId(Long memberId);
}
