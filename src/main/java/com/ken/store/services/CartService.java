package com.ken.store.services;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.ken.store.dtos.CartDto;
import com.ken.store.dtos.CartItemDto;
import com.ken.store.entities.Cart;
import com.ken.store.exceptions.CartNotFoundException;
import com.ken.store.exceptions.ProductNotFoundException;
import com.ken.store.mappers.CartMapper;
import com.ken.store.repositories.CartRepository;
import com.ken.store.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

// Spring can create instance of @Service at runtime
@Transactional
@AllArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);

        return cartDto;
    }

    public CartItemDto addToCart(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartDto = cartMapper.toDto(cart);

        return cartDto;
    }

    public CartItemDto updateCartItem(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartItem = cart.getItemById(productId);
        if (cartItem == null) {
            throw new ProductNotFoundException();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public void removeItem(UUID cartId, Long productId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeItemById(productId);
        cartRepository.save(cart);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.clear();
        cartRepository.save(cart);
    }
}
