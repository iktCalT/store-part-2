package com.ken.store.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.ken.store.dtos.OrderDto;
import com.ken.store.exceptions.OrderNotBelongToUserException;
import com.ken.store.exceptions.OrderNotExistException;
import com.ken.store.mappers.OrderMapper;
import com.ken.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var customer = authService.getCurrentUser();

        var orders = orderRepository.findByCustomerId(customer.getId());
        var ordersDto = orders.stream()
            .map(order -> orderMapper.toDto(order))
            .toList();
        return ordersDto;
    }

    public OrderDto getOrderById(Long orderId) {
        var order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new OrderNotExistException();
        }

        var customer = authService.getCurrentUser();
        if (order.getCustomer().getId() != customer.getId()) {
            throw new OrderNotBelongToUserException();
        }

        return orderMapper.toDto(order);
    }
}
