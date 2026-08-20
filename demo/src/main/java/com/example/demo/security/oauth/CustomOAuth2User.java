package com.example.demo.security.oauth;

import com.example.demo.domain.user.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final List<GrantedAuthority> authorities;
    private final String email;

    public CustomOAuth2User(UserEntity entity, Map<String, Object> attributes) {
        this.attributes = Map.copyOf(attributes);
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().name()));
        this.email = entity.getEmail();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return email;
    }
}
