package com.ken.store.mappers;

import org.mapstruct.Mapper;
import com.ken.store.dtos.OrderDto;
import com.ken.store.dtos.OrderItemDto;
import com.ken.store.entities.Order;
import com.ken.store.entities.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    OrderDto toDto(Order order);

    OrderItemDto toDto(OrderItem orderItem);
}
