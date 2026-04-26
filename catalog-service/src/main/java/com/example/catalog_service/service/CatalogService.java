package com.example.catalog_service.service;

import com.example.catalog_service.dto.CatalogDto;
import com.example.catalog_service.repository.CatalogRepository;
import java.util.ArrayList;
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

    public List<CatalogDto> getAllCatalogs() {
        return catalogRepository.findAll().stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    public List<CatalogDto> searchCatalogs(String productName) {
        return catalogRepository.findByProductNameContaining(productName).stream()
                .map(catalogMapper::toDto)
                .toList();
    }

    public List<CatalogDto> searchCatalogsV2(String keyword) {
        // TODO
        return new ArrayList<>();
    }
}
