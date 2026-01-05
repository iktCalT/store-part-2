package com.ken.store.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be greater than 0")
    @Max(value = 999, message = "Quantity must be less than 1000")
    private Integer quantity;
    // if we use int, when we don't provide quantity in the request body
    //      the default value of quantity will be 0, that's not good
    // But we want to Integer, it will be null, and we can use 
    //      @NotNull to ask users to provide value
}
