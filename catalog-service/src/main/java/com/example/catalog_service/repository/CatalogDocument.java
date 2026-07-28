package com.example.catalog_service.repository;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "catalog")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogDocument {

    @Id
    private String productId;

    private String productName;

    private Integer stock;

    private Integer unitPrice;

    private LocalDateTime createdAt;


    public static CatalogDocument from(CatalogEntity catalogEntity) {
        return CatalogDocument.builder()
                .productId(catalogEntity.getProductId())
                .productName(catalogEntity.getProductName())
                .stock(catalogEntity.getStock())
                .unitPrice(catalogEntity.getUnitPrice())
                .createdAt(catalogEntity.getCreatedAt())
                .build();
    }
}
