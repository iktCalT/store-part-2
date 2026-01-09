package com.ken.store.carts.exceptions;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException() {
        super("Cart is empty.");
    }
    
}
