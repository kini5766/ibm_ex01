package com.example.demo.security.entity;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity entity) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.enabled = entity.isEnabled();
        this.accountNonLocked = entity.isAccountNonLocked();
        this.authorities = entity.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(Role.toAuthorityName(role)))
                .toList();
    }

    @Override
    public @NonNull String getUsername() {
        return email;
    }

    // 소셜만 가입한 경우 null 가능
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}