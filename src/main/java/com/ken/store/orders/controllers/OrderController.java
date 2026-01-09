package com.ken.store.orders.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.dtos.ErrorDto;
import com.ken.store.orders.dtos.OrderDto;
import com.ken.store.orders.exceptions.OrderNotFoundException;
import com.ken.store.orders.services.OrderService;
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
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable(name = "orderId") Long orderId) {
        var orderDto = orderService.getOrder(orderId);
        return ResponseEntity.ok(orderDto);
    }
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleOrderNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorDto("Order not found.")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(Exception e) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorDto(e.getMessage()));
    }
}
