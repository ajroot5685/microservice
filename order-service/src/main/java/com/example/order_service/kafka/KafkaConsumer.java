package com.example.order_service.kafka;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.repository.OrderDocument;
import com.example.order_service.repository.OrderMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final OrderMongoRepository orderMongoRepository;

    @KafkaListener(topics = "order-create")
    public void createOrder(String message) {
        log.info("Kafka Message: -> {}", message);

        OrderDto dto;
        try {
            dto = objectMapper.readValue(message, OrderDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OrderDocument document = OrderDocument.builder()
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .productId(dto.getProductId())
                .qty(dto.getQty())
                .unitPrice(dto.getUnitPrice())
                .totalPrice(dto.getTotalPrice())
                .createdAt(dto.getCreatedAt())
                .build();

        orderMongoRepository.save(document);
    }
}
