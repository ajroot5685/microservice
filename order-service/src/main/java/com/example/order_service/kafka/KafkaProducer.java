package com.example.order_service.kafka;

import com.example.order_service.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public OrderDto send(String topic, OrderDto orderDto) {
        try {
            String jsonInString = mapper.writeValueAsString(orderDto);

            kafkaTemplate.send(topic, jsonInString);
            log.info("Kafka Producer 데이터 전송, topic: {}, orderDto: {}", topic, orderDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        return orderDto;
    }
}
