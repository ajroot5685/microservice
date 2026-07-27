package com.example.order_service.controller;

import com.example.order_service.context.UserContext;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.kafka.KafkaProducer;
import com.example.order_service.service.OrderMapper;
import com.example.order_service.service.OrderService;
import com.example.order_service.vo.ListResponse;
import com.example.order_service.vo.OrderRequest;
import com.example.order_service.vo.OrderResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final KafkaProducer kafkaProducer;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        OrderDto dto = orderMapper.toDto(orderRequest);
        dto.setUserId(UserContext.getUserId());

        OrderDto order = orderService.createOrder(dto);
        kafkaProducer.send("example-catalog-topic", order);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toResponse(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<ListResponse<OrderResponse>> getOrders(@RequestHeader("traceparent") String traceparent) {
        // traceparent 수동 주입으로 헤더 전달된 것을 확인했으나, 이를 받는 쪽에서도 제대로 처리하지 못하는 현상으로 인해
        // 같은 요청에 대해 같은 traceId로 묶는것에 실패함
        log.info("traceparent: {}", traceparent);
        List<OrderResponse> orders = orderService.getOrdersByUserId(UserContext.getUserId()).stream()
                .map(orderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ListResponse.of(orders));
    }
}
