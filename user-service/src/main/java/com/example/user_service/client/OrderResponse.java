package com.example.user_service.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class OrderResponse {

    private String productId;

    private Integer qty;

    private Integer unitPrice;

    private Integer totalPrice;

    private LocalDateTime createdAt;

    private String orderId;
}
