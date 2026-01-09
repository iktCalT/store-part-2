package com.ken.store.orders.services;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.ken.store.auth.services.AuthService;
import com.ken.store.orders.dtos.OrderDto;
import com.ken.store.orders.exceptions.OrderNotFoundException;
import com.ken.store.orders.mappers.OrderMapper;
import com.ken.store.orders.repositories.OrderRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();

        var orders = orderRepository.getOrdersWithItemsByCustomer(user);
        var ordersDto = orders.stream()
            .map(order -> orderMapper.toDto(order))
            .toList();
        return ordersDto;
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository
            .getOrderWithItem(orderId)
            .orElseThrow(OrderNotFoundException::new);

        var user = authService.getCurrentUser();
        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException(
                "You don't have access to this order");
        }

        return orderMapper.toDto(order);
    }
}
