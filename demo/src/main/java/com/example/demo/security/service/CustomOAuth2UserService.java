package com.example.demo.security.service;

import com.example.demo.domain.user.entity.SocialProviderType;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.security.entity.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(@NonNull OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        SocialProviderType registration;
        Map<String, Object> attributes;
        String username;
        String email;
        String nickname;

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (SocialProviderType.NAVER.name().equals(registrationId)) {
            registration = SocialProviderType.NAVER;
            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            email = attributes.get("email").toString();
            nickname = attributes.get("nickname").toString();
        } else if (SocialProviderType.GOOGLE.name().equals(registrationId)) {
            registration = SocialProviderType.GOOGLE;
            attributes = oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();
        } else {
            throw new OAuth2AuthenticationException("지원하지 않은 소셜 로그인입니다.");
        }

        UserEntity entity = userService.updateOrCreateSocialUser(registration, username, email, nickname);
        return new CustomOAuth2User(entity, attributes);
    }
}