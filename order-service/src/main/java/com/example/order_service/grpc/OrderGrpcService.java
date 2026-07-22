package com.example.order_service.grpc;

import com.example.order.OrderListResponse;
import com.example.order.OrderResponse;
import com.example.order.OrderServiceGrpc;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.service.OrderService;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    @Override
    public void getOrder(Empty request, StreamObserver<OrderListResponse> responseObserver) {
        String userId = GrpcServerInterceptorConfig.USER_ID_CONTEXT_KEY.get();
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);

        List<OrderResponse> orderResponses = orders.stream()
                .map(dto -> {
                    Timestamp createdAtTimestamp = Timestamp.getDefaultInstance();
                    if (dto.getCreatedAt() != null) {
                        Instant instant = dto.getCreatedAt().toInstant(ZoneOffset.UTC);
                        createdAtTimestamp = Timestamp.newBuilder()
                                .setSeconds(instant.getEpochSecond())
                                .setNanos(instant.getNano())
                                .build();
                    }

                    return OrderResponse.newBuilder()
                            .setProductId(dto.getProductId() != null ? dto.getProductId() : "")
                            .setQty(dto.getQty() != null ? dto.getQty() : 0)
                            .setUnitPrice(dto.getUnitPrice() != null ? dto.getUnitPrice() : 0)
                            .setTotalPrice(dto.getTotalPrice() != null ? dto.getTotalPrice() : 0)
                            .setCreatedAt(createdAtTimestamp)
                            .setOrderId(dto.getOrderId() != null ? dto.getOrderId() : "")
                            .build();
                })
                .toList();

        OrderListResponse response = OrderListResponse.newBuilder()
                .addAllOrders(orderResponses)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
