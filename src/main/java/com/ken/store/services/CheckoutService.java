package com.ken.store.services;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.ken.store.dtos.CheckoutDto;
import com.ken.store.entities.Order;
import com.ken.store.entities.OrderStatus;
import com.ken.store.exceptions.CartEmptyException;
import com.ken.store.exceptions.CartNotFoundException;
import com.ken.store.mappers.CheckoutMapper;
import com.ken.store.repositories.CartRepository;
import com.ken.store.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CheckoutMapper checkoutMapper;
    private final AuthService authService;

    @Transactional
    public CheckoutDto placeOrder(UUID cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException();
        }

        
        var user = authService.getCurrentUser();

        var order = new Order();
        order.setCustomer(user);
        order.setStatus(OrderStatus.PENDING);
        cart.getItems().stream()
            .forEach(cartItem -> order.addItem(cartItem));
        orderRepository.save(order);
        var orderDto = checkoutMapper.toDto(order);

        cart.clear();
        cartRepository.save(cart);

        return orderDto;
    }
}
