package com.example.order_service.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderDto {

    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private String orderId;
    private String userId;
    private LocalDateTime createdAt;
}
