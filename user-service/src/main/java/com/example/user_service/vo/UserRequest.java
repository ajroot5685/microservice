package com.example.user_service.vo;

import lombok.Data;

@Data
public class UserRequest {

    private String email;

    private String pwd;

    private String name;
}
