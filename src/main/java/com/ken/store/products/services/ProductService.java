package com.ken.store.products.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ken.store.products.dtos.ProductDto;
import com.ken.store.products.entities.Product;
import com.ken.store.products.exceptions.CategoryNotFoundException;
import com.ken.store.products.exceptions.ProductNotFoundException;
import com.ken.store.products.mappers.ProductMapper;
import com.ken.store.products.repositories.CategoryRepository;
import com.ken.store.products.repositories.ProductRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getProductsByCategoryId(Byte categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }

        return products.stream().map(productMapper::toDto).toList();
    }

    public ProductDto getProductById(Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            throw new ProductNotFoundException();
        else
            return productMapper.toDto(product);
    }

    public Product createProduct(ProductDto productDto) {
        var category = categoryRepository.findById(
                productDto.getCategoryId()).orElse(null);
        if (category == null) {
            throw new ProductNotFoundException();
        }

        var product = productMapper.toEntity(productDto);
        product.setCategory(category);

        productRepository.save(product);
        
        return product;
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        var category = categoryRepository.findById(
                productDto.getCategoryId()).orElse(null);
        if (category == null) {
            throw new CategoryNotFoundException();
        }

        productMapper.update(productDto, product);
        product.setCategory(category);
        productRepository.save(product);

        return productMapper.toDto(product);
    }

    public void deleteProduct(Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        productRepository.delete(product);
    }
}
