package com.ken.store.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDto {
    private CartProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;
}
