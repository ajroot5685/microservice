package com.example.catalog_service.controller;

import com.example.catalog_service.service.CatalogMapper;
import com.example.catalog_service.service.CatalogService;
import com.example.catalog_service.vo.CatalogResponse;
import com.example.catalog_service.vo.ListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;

    @GetMapping
    public ResponseEntity<ListResponse<CatalogResponse>> getCatalogs() {
        List<CatalogResponse> response = catalogService.getAllCatalogs().stream()
                .map(catalogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ListResponse.of(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CatalogResponse> getCatalog(@PathVariable(value = "productId") String productId) {
        CatalogResponse response = catalogMapper.toResponse(catalogService.getCatalog(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ListResponse<CatalogResponse>> searchCatalogs(@RequestParam(value = "keyword") String keyword) {
        List<CatalogResponse> response = catalogService.searchCatalogs(keyword).stream()
                .map(catalogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ListResponse.of(response));
    }

    @GetMapping("/search/ES")
    public ResponseEntity<ListResponse<CatalogResponse>> searchCatalogsFromES(@RequestParam(value = "keyword") String keyword) {
        List<CatalogResponse> response = catalogService.searchCatalogsFromES(keyword).stream()
                .map(catalogMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ListResponse.of(response));
    }
}
