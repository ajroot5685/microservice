package com.example.order_service.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    private String getQueueKey(Long productId) {
        return "queue:" + productId;
    }

    public Long register(Long productId, String userId) {
        String key = getQueueKey(productId);

        redisTemplate.opsForZSet().add(
                key,
                userId,
                System.currentTimeMillis()
        );

        Long rank = redisTemplate.opsForZSet()
                .rank(key, userId);

        return rank == null ? -1 : rank + 1;
    }

    public Long getRank(Long productId, String userId) {
        Long rank = redisTemplate.opsForZSet()
                .rank(getQueueKey(productId), userId);

        return rank == null ? -1 : rank + 1;
    }

    public Set<String> getTopUsers(Long productId, int count) {
        return redisTemplate.opsForZSet()
                .range(getQueueKey(productId), 0, count - 1);
    }

    public void remove(Long productId, String userId) {
        redisTemplate.opsForZSet()
                .remove(getQueueKey(productId), userId);
    }
}
