package com.example.demo.security.entity;

import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.entity.UserSocialAccountEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@NullMarked
public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final List<GrantedAuthority> authorities;
    private final String sub;

    public CustomOAuth2User(Map<String, Object> attributes, UserEntity user, UserSocialAccountEntity socialAccount) {
        this.attributes = Map.copyOf(attributes);
        this.authorities = CustomUserDetails.toAuthorities(user.getRoles());
        this.sub = String.valueOf(user.getId());
    }

    // jwt 토큰 sub
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
        return sub;
    }
}
