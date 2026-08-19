package com.example.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    // 소셜만 있으면 null
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType;

    @Column(name = "is_lock", nullable = false)
    private boolean isLocked;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSocialAccountEntity> socialAccounts = new ArrayList<>();

    @Builder
    public UserEntity(String username, String password, UserRoleType roleType, boolean isLocked, String nickname, String email) {
        this.username = username;
        this.password = password;
        this.roleType = roleType;
        this.isLocked = isLocked;
        this.nickname = nickname;
        this.email = email;
    }
}
