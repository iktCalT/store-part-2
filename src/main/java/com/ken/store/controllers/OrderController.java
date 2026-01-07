package com.ken.store.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.dtos.OrderDto;
import com.ken.store.exceptions.OrderNotBelongToUserException;
import com.ken.store.exceptions.OrderNotExistException;
import com.ken.store.services.OrderService;
import lombok.AllArgsConstructor;



@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        var ordersDto = orderService.getAllOrders();
        return ResponseEntity.ok(ordersDto);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable(name = "orderId") Long orderId) {
        var orderDto = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDto);
    }
    
    @ExceptionHandler(OrderNotExistException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of("error", "Order was not found.")
        );
    }

    @ExceptionHandler(OrderNotBelongToUserException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotBelongToUser() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Map.of("error", "This is not your order.")
        );
    }
}
