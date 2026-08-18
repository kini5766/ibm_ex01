package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.entity.SocialProviderType;
import com.example.demo.domain.user.entity.UserEntity;

import java.util.Map;

public interface UserService {
    UserEntity updateOrCreateSocialUser(SocialProviderType registration, String username, String email, String nickname);

    UserEntity existsUser(String username);

    Long addUser(UserRequestDTO dto);
}
