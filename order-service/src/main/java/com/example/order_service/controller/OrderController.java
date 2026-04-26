package com.example.order_service.controller;

import com.example.order_service.context.UserContext;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.service.OrderMapper;
import com.example.order_service.service.OrderService;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        OrderDto dto = orderMapper.toDto(orderRequest);
        dto.setUserId(UserContext.getUserId());

        OrderDto order = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.toResponse(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> orders = orderService.getOrdersByUserId(UserContext.getUserId()).stream()
                .map(orderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(orders);
    }
}
