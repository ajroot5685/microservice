package com.example.order_service.repository;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDocument {

    @Id
    private String id;

    private String orderId;

    @Indexed
    private String userId;

    private String productId;

    private Integer qty;

    private Integer unitPrice;

    private Integer totalPrice;

    private LocalDateTime createdAt;
}
