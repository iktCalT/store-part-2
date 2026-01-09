package com.ken.store.orders.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ken.store.orders.entities.OrderStatus;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;
}
