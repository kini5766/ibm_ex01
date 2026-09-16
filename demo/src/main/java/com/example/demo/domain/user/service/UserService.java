package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.dto.UserResponseDTO;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean existsUser(String email) {
        return userRepository.existsByEmail(email);
    }

    public Long addUser(UserRequestDTO dto) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            throw new IllegalStateException("기존에 존재하는 회원입니다.");
        }
        String password = passwordEncoder.encode(dto.getPassword());
        UserEntity user = userRepository.save(dto.toEntity(password));
        return user.getId();
    }

    public UserResponseDTO readUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new BadCredentialsException("invalid refresh token"));
        return new UserResponseDTO(user.getEmail(), user.getNickname());
    }

    public UserResponseDTO updateUser(UserRequestDTO dto) throws AccessDeniedException {
        return null;
    }

    public void deleteUser(UserRequestDTO dto) throws AccessDeniedException {

    }
}
