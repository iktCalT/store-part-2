package com.ken.store.products.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.ken.store.common.dtos.ErrorDto;
import com.ken.store.products.dtos.ProductDto;
import com.ken.store.products.exceptions.CategoryNotFoundException;
import com.ken.store.products.exceptions.ProductNotFoundException;
import com.ken.store.products.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;



@AllArgsConstructor
@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products (in a category)")
    @GetMapping("")
    public List<ProductDto> getProductsByCategoryId(
            @RequestParam(required = false, name = "categoryId") Byte categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @Operation(summary = "Get a product")
    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Create a product")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder) {
        var product = productService.createProduct(productDto);
        
        var uri = uriBuilder.path("/products/{id}")
                .buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Update product's information")
    @PutMapping("/{id}")
    public ProductDto updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorDto("Product not found.")
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCategoryNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorDto("Category not found.")
        );
    }
}
