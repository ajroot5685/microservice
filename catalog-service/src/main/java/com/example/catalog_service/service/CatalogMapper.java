package com.example.catalog_service.service;

import com.example.catalog_service.dto.CatalogDto;
import com.example.catalog_service.repository.CatalogEntity;
import com.example.catalog_service.vo.CatalogResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogMapper {

    CatalogDto toDto(CatalogEntity catalogEntity);

    CatalogResponse toResponse(CatalogDto catalogDto);
}
