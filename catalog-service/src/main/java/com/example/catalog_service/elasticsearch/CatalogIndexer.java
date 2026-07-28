package com.example.catalog_service.elasticsearch;

import com.example.catalog_service.repository.CatalogDocument;
import com.example.catalog_service.repository.CatalogRepository;
import com.example.catalog_service.repository.CatalogSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogIndexer {

    private final CatalogRepository catalogRepository;
    private final CatalogSearchRepository searchRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        searchRepository.deleteAll();

        List<CatalogDocument> documents = catalogRepository.findAll()
                .stream()
                .map(CatalogDocument::from)
                .toList();

        searchRepository.saveAll(documents);
        log.info("ES 인덱싱 개수: {}", documents.size());
    }
}
