package com.ken.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.ken.store.dtos.CartDto;
import com.ken.store.dtos.CartItemDto;
import com.ken.store.entities.Cart;
import com.ken.store.entities.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    // We can implement expressions in the following way
    // we can use two @Mapping to annotate one method
    // @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
