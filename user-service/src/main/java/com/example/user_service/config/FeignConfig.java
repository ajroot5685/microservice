package com.example.user_service.config;

import com.example.user_service.context.UserContextFilter;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            if (UserContextFilter.USER_ID.isBound()) {
                String userId = UserContextFilter.USER_ID.get();
                if (userId != null && !userId.isEmpty()) {
                    template.header("userId", userId);
                    return;
                }
            }

            log.warn("허용되지 않은 플로우(user id 컨텍스트 필요)");
        };
    }
}
