package com.example.demo.domain.user.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class UserSocialAccountEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProviderType provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    private String email;
}
