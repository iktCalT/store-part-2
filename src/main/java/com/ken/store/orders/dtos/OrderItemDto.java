package com.ken.store.orders.dtos;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDto {
    private ProductDto product;
    private Integer quantity;
    private BigDecimal totalPrice;
}
