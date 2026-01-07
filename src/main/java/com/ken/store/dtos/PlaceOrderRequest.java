package com.ken.store.dtos;

import java.util.UUID;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    private UUID cartId;
}
