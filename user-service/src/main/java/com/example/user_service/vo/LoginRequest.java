package com.example.user_service.vo;

import lombok.Data;

@Data
public class LoginRequest {

    private String userId;

    private String pwd;
}
