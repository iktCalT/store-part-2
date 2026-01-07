package com.ken.store.mappers;

import org.mapstruct.Mapper;
import com.ken.store.dtos.CheckoutDto;
import com.ken.store.entities.Order;

@Mapper(componentModel = "spring")
public interface CheckoutMapper {
    CheckoutDto toDto(Order order);
}
