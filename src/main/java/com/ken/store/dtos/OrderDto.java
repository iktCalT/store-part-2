package com.ken.store.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.ken.store.entities.OrderItem;
import com.ken.store.entities.OrderStatus;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
}
