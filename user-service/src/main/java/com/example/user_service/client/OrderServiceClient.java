package com.example.user_service.client;

import java.util.List;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface OrderServiceClient {

    @GetExchange("/orders")
    List<OrderResponse> getOrders();
}
