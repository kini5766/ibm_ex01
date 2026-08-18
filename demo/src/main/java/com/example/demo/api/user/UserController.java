package com.example.demo.api.user;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public static final String EXIST_URL = "/api/user/exist";
    public static final String USER_URL = "/api/user";

    // 자체 로그인 유저 존재 확인
    // http://localhost:8081/api/user/exist
    @GetMapping(value = EXIST_URL + "/{username}")
    public ResponseEntity<UserEntity> existUserApi(@PathVariable String username) {
        return ResponseEntity.ok(userService.existsUser(username));
    }

    // 회원가입
    // http://localhost:8081/api/user
    @PostMapping(value = USER_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> joinApi(
            @Validated({UserRequestDTO.addGroup.class}) @RequestBody UserRequestDTO dto
    ) {
        Long id = userService.addUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
