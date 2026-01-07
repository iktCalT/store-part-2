package com.ken.store.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.dtos.PlaceOrderRequest;
import com.ken.store.exceptions.CartEmptyException;
import com.ken.store.exceptions.CartNotFoundException;
import com.ken.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<?> placeOrder(
            @Valid @RequestBody PlaceOrderRequest placeOrderRequest) {

        var orderDto = checkoutService.placeOrder(placeOrderRequest.getCartId());
        return ResponseEntity.ok(orderDto);
    }
    
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of("error", "Cart not found.")
        );
    }

    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<Map<String, String>> handleCartEmpty() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of("error", "Cart is empty.")
        );
    }
}
