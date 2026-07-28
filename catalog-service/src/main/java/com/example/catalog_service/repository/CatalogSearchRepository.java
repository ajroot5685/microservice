package com.example.catalog_service.repository;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CatalogSearchRepository extends ElasticsearchRepository<CatalogDocument, String> {

    List<CatalogDocument> findByProductNameContaining(String keyword);
}
