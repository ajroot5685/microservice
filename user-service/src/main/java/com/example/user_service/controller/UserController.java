package com.example.user_service.controller;

import com.example.user_service.dto.UserDto;
import com.example.user_service.service.JwtTokenProvider;
import com.example.user_service.service.UserMapper;
import com.example.user_service.service.UserService;
import com.example.user_service.vo.LoginRequest;
import com.example.user_service.vo.UserRequest;
import com.example.user_service.vo.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserDto userDto = userService.createUser(userMapper.toDto(request));
        UserResponse response = userMapper.toResponse(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> response = userService.getUserByAll().stream()
                .map(userMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        return ResponseEntity.ok(userMapper.toResponse(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUserId(),
                request.getPwd());
        Authentication authenticate = authenticationManager.authenticate(token);

        String jwt = jwtTokenProvider.createToken(authenticate.getName());
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwt)
                .body("로그인 성공");
    }
}
