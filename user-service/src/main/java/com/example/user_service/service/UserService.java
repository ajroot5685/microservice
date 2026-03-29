package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.repository.UserEntity;
import com.example.user_service.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        UserEntity user = userMapper.toEntity(userDto);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public List<UserDto> getUserByAll() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return userMapper.toDto(user);
    }
}
