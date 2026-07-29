package com.example.order_service.redis;

import com.example.order_service.repository.OrderRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * sorted set 대기열 테스트용 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisInitializer {

    private final RedisTemplate<String, String> redisTemplate;
    private final OrderRepository orderRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String catalogKey = "catalog:1";
        String queueKey = "queue:1";

        redisTemplate.delete(catalogKey);
        redisTemplate.delete(queueKey);
        orderRepository.deleteAll();

        Map<String, String> catalog = Map.of(
                "productId", "1",
                "productName", "RGB 기계식 게이밍 키보드 Model-1",
                "stock", "20",
                "unitPrice", "1000"
        );
        redisTemplate.opsForHash().putAll(catalogKey, catalog);

        log.info("redis 초기화 완료");
    }
}
