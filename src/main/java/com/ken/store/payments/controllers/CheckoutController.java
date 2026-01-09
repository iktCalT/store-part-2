package com.ken.store.payments.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.carts.exceptions.CartEmptyException;
import com.ken.store.carts.exceptions.CartNotFoundException;
import com.ken.store.common.dtos.ErrorDto;
import com.ken.store.payments.dtos.CheckoutRequest;
import com.ken.store.payments.dtos.CheckoutResponse;
import com.ken.store.payments.exceptions.PaymentException;
import com.ken.store.payments.services.CheckoutService;
import com.ken.store.payments.services.WebhookRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    // checkout = place an order
    @PostMapping
    public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request.getCartId());
    }

    // Make sure request comes from stripe / paypal / ...
    // and it hasn't been changed
    @PostMapping("/webhook")
    public void handleWebhookRequest(
        @RequestHeader Map<String, String> header,
        @RequestBody String payload
    ) {
        checkoutService.handleWebhookRequest(new WebhookRequest(header, payload));
    }
    
    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleCartEmptyException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorDto(e.getMessage())
        );
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDto> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }
}
