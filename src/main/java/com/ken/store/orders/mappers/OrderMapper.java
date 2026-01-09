package com.ken.store.orders.mappers;

import org.mapstruct.Mapper;
import com.ken.store.orders.dtos.OrderDto;
import com.ken.store.orders.dtos.OrderItemDto;
import com.ken.store.orders.entities.Order;
import com.ken.store.orders.entities.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    OrderDto toDto(Order order);

    OrderItemDto toDto(OrderItem orderItem);
}
