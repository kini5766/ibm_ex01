package com.example.demo.api.user;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.dto.UserResponseDTO;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public static final String EXIST_URL = "/api/user/exist";
    public static final String USER_URL = "/api/user";

    // 아이디 중복 확인
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

    // 회원 정보
    // http://localhost:8081/api/user
    @GetMapping(value = USER_URL)
    public ResponseEntity<UserResponseDTO> userMeApi() {
        return ResponseEntity.ok(userService.readUser());
    }

    // 회원 수정
    // http://localhost:8081/api/user
    @PutMapping(value = USER_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> updateUserApi(
            @Validated({UserRequestDTO.updateGroup.class}) @RequestBody UserRequestDTO dto
    ) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(dto));
    }


    // 회원 탈퇴
    // http://localhost:8081/api/user
    @DeleteMapping(value = USER_URL)
    public ResponseEntity<Boolean> deleteUserApi(
            @Validated({UserRequestDTO.deleteGroup.class}) @RequestBody UserRequestDTO dto
    ) throws AccessDeniedException {
        userService.deleteUser(dto);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
