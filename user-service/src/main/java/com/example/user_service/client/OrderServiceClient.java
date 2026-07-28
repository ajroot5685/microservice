package com.example.user_service.client;

import com.example.user_service.vo.ListResponse;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface OrderServiceClient {

    @GetExchange("/orders")
    ListResponse<OrderResponse> getOrders();

    @GetExchange("/orders/mongo")
    ListResponse<OrderResponse> getOrdersFromMongo();
}
