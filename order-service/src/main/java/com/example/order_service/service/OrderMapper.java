package com.example.order_service.service;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.repository.OrderEntity;
import com.example.order_service.vo.OrderRequest;
import com.example.order_service.vo.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderEntity toEntity(OrderDto orderDto);

    OrderDto toDto(OrderEntity orderEntity);

    OrderDto toDto(OrderRequest orderRequest);

    OrderResponse toResponse(OrderDto orderDto);
}
