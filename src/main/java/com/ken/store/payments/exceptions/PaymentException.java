package com.ken.store.payments.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }
    
}
