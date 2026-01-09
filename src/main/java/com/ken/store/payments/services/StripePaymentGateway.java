package com.ken.store.payments.services;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ken.store.orders.entities.Order;
import com.ken.store.orders.entities.OrderItem;
import com.ken.store.orders.entities.OrderStatus;
import com.ken.store.payments.exceptions.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Builder;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import com.stripe.param.checkout.SessionCreateParams.Mode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StripePaymentGateway implements PaymentGateway {
    
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            // Create a checkout session
            var builder = SessionCreateParams.builder()
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                .setCancelUrl(websiteUrl + "/cancel.html")
                .putMetadata("order_id", order.getId().toString());

            // for each item in order, create a stripe line item
            order.getItems().forEach(item -> addLineItem(builder, item));

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException e) {
            throw new PaymentException();
        }
    }

    private void addLineItem(Builder builder, OrderItem item) {
        var lineItem = SessionCreateParams.LineItem.builder()
            .setQuantity(Long.valueOf(item.getQuantity()))
            .setPriceData(createPriceData(item)).build();

        builder.addLineItem(lineItem);
    }

    private PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency("usd")
            .setUnitAmountDecimal(
                item.getUnitPrice().multiply(BigDecimal.valueOf(100))) // This value has to be in smallest currency unit (cent)
            .setProductData(createProductData(item))
            .build();
    }

    private ProductData createProductData(OrderItem item) {
        return ProductData.builder()
            .setName(item.getProduct().getName())
            .build();
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeader().get("Stripe-Signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            
            // `case __ -> [code]` equals to `case: ___ [code] break;`
            return switch (event.getType()) {
                // "checkout.session.completed" contains: paid and unpaid
                case "checkout.session.completed" -> 
                    Optional.of(extractFromSession(event));

                case "payment_intent.succeeded" -> 
                    Optional.of(new PaymentResult(extractOrderIdFromIntent(event), OrderStatus.PAID));
                
                case "payment_intent.payment_failed" -> 
                    Optional.of(new PaymentResult(extractOrderIdFromIntent(event), OrderStatus.FAILED));
                
                default -> Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid signature.");
        }
    }

    private PaymentResult extractFromSession(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
            () -> new PaymentException("Could not deserialize Stripe event. Check SDK and API version")
        );

        var session = (Session) stripeObject;
        var orderId = session.getMetadata().get("order_id");
        if ("paid".equals(session.getPaymentStatus())) {
            return new PaymentResult(Long.valueOf(orderId), OrderStatus.PAID);
        } else {
            return new PaymentResult(Long.valueOf(orderId), OrderStatus.PENDING);
        }
    }

    private Long extractOrderIdFromIntent(Event event) {
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
            () -> new PaymentException("Could not deserialize Stripe event. Check SDK and API version")
        );

        var paymentIntent = (PaymentIntent) stripeObject;
        var orderId = paymentIntent.getMetadata().get("order_id");
        return Long.valueOf(orderId);
    }
}
