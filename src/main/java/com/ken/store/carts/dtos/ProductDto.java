package com.ken.store.carts.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
// Product DTO shown in cart (a concise version of product DTO)
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
}
