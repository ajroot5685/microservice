package com.example.order_service.context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContext {

    public static String getUserId() {
        if (!UserContextFilter.USER_ID.isBound()) {
            log.error("user id 컨텍스트가 설정되지 않았습니다.");
            throw new RuntimeException();
        }
        return UserContextFilter.USER_ID.get();
    }
}
