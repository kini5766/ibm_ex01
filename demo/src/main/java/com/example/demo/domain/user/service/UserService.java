package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.dto.UserResponseDTO;
import com.example.demo.domain.user.entity.SocialProviderType;
import com.example.demo.domain.user.entity.UserEntity;

import java.nio.file.AccessDeniedException;

public interface UserService {
    UserEntity updateOrCreateSocialUser(SocialProviderType registration, String username, String email, String nickname);

    UserEntity existsUser(String username);

    Long addUser(UserRequestDTO dto);

    UserResponseDTO readUser();

    UserResponseDTO updateUser(UserRequestDTO dto) throws AccessDeniedException;

    void deleteUser(UserRequestDTO dto) throws AccessDeniedException;
}
