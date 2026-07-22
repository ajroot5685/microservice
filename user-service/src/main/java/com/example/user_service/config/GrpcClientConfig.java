package com.example.user_service.config;

import com.example.order.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @GrpcClient("orderService")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub() {
        return this.orderServiceStub;
    }
}
