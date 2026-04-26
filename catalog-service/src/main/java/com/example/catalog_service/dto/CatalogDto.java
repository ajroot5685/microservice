package com.example.catalog_service.dto;

import lombok.Data;

@Data
public class CatalogDto {

    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;

    private Integer totalPrice;
    private String orderId;
    private String userId;
}
