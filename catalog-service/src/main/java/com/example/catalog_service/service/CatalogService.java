package com.example.catalog_service.service;

import com.example.catalog_service.dto.CatalogDto;
import com.example.catalog_service.repository.CatalogEntity;
import com.example.catalog_service.repository.CatalogRepository;
import com.example.catalog_service.repository.CatalogSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;
    private final CatalogSearchRepository searchRepository;

    public List<CatalogDto> getAllCatalogs() {
        return catalogRepository.findAll().stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    public CatalogDto getCatalog(String productId) {
        CatalogEntity catalog = catalogRepository.findByProductId(productId);
        return catalogMapper.toDto(catalog);
    }

    public List<CatalogDto> searchCatalogs(String productName) {
        return catalogRepository.findByProductNameContaining(productName).stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    public List<CatalogDto> searchCatalogsFromES(String keyword) {
        return searchRepository.findByProductNameContaining(keyword).stream()
                .map(catalogMapper::toDto)
                .toList();
    }
}
