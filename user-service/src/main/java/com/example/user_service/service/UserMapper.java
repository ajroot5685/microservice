package com.example.user_service.service;

import com.example.user_service.dto.UserDto;
import com.example.user_service.repository.UserEntity;
import com.example.user_service.vo.UserRequest;
import com.example.user_service.vo.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDto userDto);

    UserDto toDto(UserEntity userEntity);

    UserDto toDto(UserRequest userRequest);

    UserResponse toResponse(UserDto userDto);
}
