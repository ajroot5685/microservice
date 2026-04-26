package com.example.catalog_service.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class CatalogResponse {

    private String productId;

    private String productName;

    private Integer unitPrice;

    private Integer stock;

    private Date createdAt;
}
