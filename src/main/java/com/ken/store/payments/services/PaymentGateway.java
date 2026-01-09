package com.ken.store.payments.services;

import java.util.Optional;
import com.ken.store.orders.entities.Order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);

    // WebhookRequest(payload, header) -> PaymentResult (orderId, orderStatus)
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}
