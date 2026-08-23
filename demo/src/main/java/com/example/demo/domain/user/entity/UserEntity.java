package com.example.demo.domain.user.entity;

import com.example.demo.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Builder
@ToString
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    // 소셜만 있으면 null
    @ToString.Exclude
    @Column
    private String password;

    // 활성화 여부 (비활성화 / 회원탈퇴)
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // 계정 잠금 여부
    @Column(nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSocialAccountEntity> socialAccounts = new ArrayList<>();
}
