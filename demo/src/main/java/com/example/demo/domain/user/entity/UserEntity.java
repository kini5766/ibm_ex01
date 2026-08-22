package com.example.demo.domain.user.entity;

import com.example.demo.common.entity.BaseTimeEntity;
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

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    // 소셜만 있으면 null
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role;

    @Column(nullable = false)
    private boolean isLocked;

    private String nickname;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSocialAccountEntity> socialAccounts = new ArrayList<>();

    @Builder
    public UserEntity(String name, String password, UserRoleType role, boolean isLocked, String nickname, String email) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.isLocked = isLocked;
        this.nickname = nickname;
        this.email = email;
    }
}
