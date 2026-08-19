package com.example.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Table(
        name = "user_social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialAccountEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private SocialProviderType providerType;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "email")
    private String email;
}
