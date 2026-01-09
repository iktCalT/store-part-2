package com.ken.store.payments.services;

import java.util.UUID;
import org.springframework.stereotype.Service;
// import jakarta.transaction.Transactional; // NOT this one!
import org.springframework.transaction.annotation.Transactional;
import com.ken.store.auth.services.AuthService;
import com.ken.store.carts.exceptions.CartEmptyException;
import com.ken.store.carts.exceptions.CartNotFoundException;
import com.ken.store.carts.repositories.CartRepository;
import com.ken.store.carts.services.CartService;
import com.ken.store.orders.entities.Order;
import com.ken.store.orders.repositories.OrderRepository;
import com.ken.store.payments.dtos.CheckoutResponse;
import com.ken.store.payments.exceptions.PaymentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor // only fields with final will be initialized
@Slf4j
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway; 
    // Spring will automatically inject dependency (like StripePaymentGateway / PaypalPaymentGateway / ...)

    @Transactional
    public CheckoutResponse checkout(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if (cart.isEmpty()) {
            throw new CartEmptyException();
        }
        
        var order = new Order();
        order.fromOrder(cart, authService.getCurrentUser());

        orderRepository.save(order);

        try {
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cartId);
            
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException e) {
            log.info("Unable to create order, reason: " + e.getMessage());
            orderRepository.delete(order);
            throw e;
        }
    }

    public void handleWebhookRequest(WebhookRequest request) {
        paymentGateway
            .parseWebhookRequest(request)
            .ifPresent(paymentResult -> {
                var order = orderRepository.findById(paymentResult.getOrderId()).orElse(null);
                order.setStatus(paymentResult.getOrderStatus());
                orderRepository.save(order);
            });
    }
}
