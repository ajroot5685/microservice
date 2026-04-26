package com.example.catalog_service.controller;

import com.example.catalog_service.service.CatalogMapper;
import com.example.catalog_service.service.CatalogService;
import com.example.catalog_service.vo.CatalogResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;

    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogResponse>> getCatalogs() {
        List<CatalogResponse> response = catalogService.getAllCatalogs().stream()
                .map(catalogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/catalogs/search")
    public ResponseEntity<List<CatalogResponse>> searchCatalogs(@RequestParam(value = "keyword") String keyword) {
        List<CatalogResponse> response = catalogService.searchCatalogs(keyword).stream()
                .map(catalogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
