package com.ken.store.products.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.ken.store.products.dtos.ProductDto;
import com.ken.store.products.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // source: Entity
    // target: DTO
    // map product.category.id to productDto.categoryId
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    // Ignore mapping id 
    // the id of productDto is null, we should not map it to product
    Product update(ProductDto productDto, @MappingTarget Product product);
}
