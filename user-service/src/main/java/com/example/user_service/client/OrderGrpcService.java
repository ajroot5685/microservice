package com.example.user_service.client;

import com.example.order.OrderListResponse;
import com.example.order.OrderServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGrpcService {

    private final OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public List<OrderResponse> getOrders() {
        OrderListResponse response = orderServiceStub.getOrder(Empty.getDefaultInstance());
        return response.getOrdersList().stream()
                .map(this::mapToDto)
                .toList();
    }

    private OrderResponse mapToDto(com.example.order.OrderResponse order) {
        LocalDateTime createdAt = null;
        if (order.hasCreatedAt()) {
            Timestamp timestamp = order.getCreatedAt();
            createdAt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()),
                    ZoneOffset.UTC
            );
        }

        OrderResponse response = new OrderResponse();
        response.setProductId(order.getProductId());
        response.setQty(order.getQty());
        response.setUnitPrice(order.getUnitPrice());
        response.setTotalPrice(order.getTotalPrice());
        response.setOrderId(order.getOrderId());
        response.setCreatedAt(createdAt);

        return response;
    }
}
