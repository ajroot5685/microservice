package com.example.catalog_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    CatalogEntity findByProductId(String productId);

    List<CatalogEntity> findByProductNameContaining(String productName);
}
