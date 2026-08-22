package com.example.demo.domain.user.entity;

import com.example.demo.common.entity.BaseTimeEntity;
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
    @Column(nullable = false)
    private SocialProviderType providerType;

    @Column(nullable = false)
    private String providerId;

    private String email;
}
