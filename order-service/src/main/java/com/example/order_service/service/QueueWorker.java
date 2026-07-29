package com.example.order_service.service;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.repository.OrderEntity;
import com.example.order_service.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueWorker {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<String> popQueueScript;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 500)
    public void processQueue() {
        String catalogKey = "catalog:1";
        String queueKey = "queue:1";

        String userId = redisTemplate.execute(
                popQueueScript,
                List.of(catalogKey, queueKey)
        );

        if (userId == null) {
            log.info("QueueWorker - userId is null(대기열에 유저가 존재하지 않거나 재고가 다 소진됨)");
            return;
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(catalogKey);

        Integer price = Integer.valueOf(entries.get("unitPrice").toString());
        OrderDto orderDto = OrderDto.builder()
                .productId(entries.get("productId").toString())
                .qty(1)
                .unitPrice(price)
                .totalPrice(price)
                .orderId(UUID.randomUUID().toString())
                .userId(userId)
                .build();

        OrderEntity order = orderRepository.save(orderMapper.toEntity(orderDto));
        log.info("QueueWorker - order 저장됨: {}", order);
    }
}
