package com.ken.store.carts.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CartItemDto {
    private ProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;
}
