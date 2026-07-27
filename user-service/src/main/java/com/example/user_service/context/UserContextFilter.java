package com.example.user_service.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContextFilter implements Filter {

    public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    private final List<String> whiteList = List.of("/actuator/health", "/signup", "/login");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        boolean isWhiteList = whiteList.stream().anyMatch(requestURI::startsWith);
        if (isWhiteList) {
            chain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader("userId");

        if (userId == null || userId.isEmpty()) {
            log.warn("유효하지 않은 요청입니다. user id가 존재하지 않습니다. URI: {}", requestURI);

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "userId 헤더가 없습니다.");
            return;
        }

        ScopedValue.where(USER_ID, userId).run(() -> {
            try {
                chain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                log.error("에러 발생", e);
                throw new RuntimeException(e);
            }
        });
    }
}
