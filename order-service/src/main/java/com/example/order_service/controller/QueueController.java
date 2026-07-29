package com.example.order_service.controller;

import com.example.order_service.context.UserContext;
import com.example.order_service.service.QueueService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/{productId}/queue")
    public ResponseEntity<Long> register(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(queueService.register(productId, UserContext.getUserId()));
    }

    @GetMapping("/{productId}/queue")
    public ResponseEntity<Long> getRank(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(queueService.getRank(productId, UserContext.getUserId()));
    }

    @GetMapping("/{productId}/queue/top")
    public ResponseEntity<Set<String>> getTopUsers(@PathVariable("productId") Long productId,
                                                   @RequestParam("count") int count) {
        return ResponseEntity.ok(queueService.getTopUsers(productId, count));
    }
}
