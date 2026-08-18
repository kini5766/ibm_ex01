package com.example.demo.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // 또는 email

    private String password; // 소셜만 있으면 null

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSocialAccountEntity> socialAccounts = new ArrayList<>();
}
