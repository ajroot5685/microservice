package com.example.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HelloController {

    private final Environment env;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port"));
    }

    @GetMapping("/user-service/hello")
    public String hello(@RequestHeader("s-request") String header) {
        log.info("header: {}", header);
        return "hello";
    }
}
