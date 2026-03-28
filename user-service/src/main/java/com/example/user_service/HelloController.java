package com.example.user_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/user-service/hello")
    public String hello(@RequestHeader("s-request") String header) {
        log.info("header: {}", header);
        return "hello";
    }
}
