package com.example.order_service.service;

import com.example.order_service.client.CatalogResponse;
import com.example.order_service.client.CatalogServiceClient;
import com.example.order_service.dto.OrderDto;
import com.example.order_service.exception.CatalogNotFoundException;
import com.example.order_service.exception.OutOfStockException;
import com.example.order_service.repository.OrderDocument;
import com.example.order_service.repository.OrderEntity;
import com.example.order_service.repository.OrderMongoRepository;
import com.example.order_service.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderMongoRepository orderMongoRepository;

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        CatalogResponse catalog = catalogServiceClient.getCatalog(orderDto.getProductId());
        if (catalog == null) {
            throw new CatalogNotFoundException("존재하지 않는 상품입니다.");
        }
        if (catalog.getStock() == null || catalog.getStock() < orderDto.getQty()) {
            throw new OutOfStockException("재고가 부족합니다. 요청 재고: " + orderDto.getQty() + " 현재 재고: " + catalog.getStock());
        }

        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        OrderEntity order = orderMapper.toEntity(orderDto);

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId);
        return orderMapper.toDto(order);
    }

    public List<OrderDto> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public List<OrderDocument> getOrderDocumentsByUserId(String userId) {
        return orderMongoRepository.findByUserId(userId);
    }
}
