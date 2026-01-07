package com.ken.store.controllers;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.ken.store.dtos.AddItemToCartRequest;
import com.ken.store.dtos.CartDto;
import com.ken.store.dtos.CartItemDto;
import com.ken.store.dtos.UpdateCartItemRequest;
import com.ken.store.exceptions.CartNotFoundException;
import com.ken.store.exceptions.ProductNotFoundException;
import com.ken.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts") // name shown in swagger openAPI
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Create a cart")
    @PostMapping
    public ResponseEntity<CartDto> createCart(
        UriComponentsBuilder uriBuilder
    ) {
        var cartDto = cartService.createCart();

        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @Operation(summary = "Add an item to a cart")
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @Parameter(description = "Cart's ID") @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request) {
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
    
    @Operation(summary = "Get all items in a cart")
    @GetMapping("/{cartId}")
    public CartDto getCart(
        @Parameter(description = "Cart's ID") @PathVariable UUID cartId) {
        return cartService.getCart(cartId);
    }
    
    @Operation(summary = "Update the quantity of an item in a cart")
    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateCartItem(
            @Parameter(description = "Cart's ID") @PathVariable("cartId") UUID cartId,
            @Parameter(description = "Product's ID") @PathVariable("productId") Long productId,  
            @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateCartItem(
                cartId, productId, request.getQuantity());
        // return ResponseEntity.ok(cartItemDto);
    }

    @Operation(summary = "Remove an item from a cart")
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
        @Parameter(description = "Cart's ID") @PathVariable("cartId") UUID cartId,
        @Parameter(description = "Product's ID") @PathVariable("productId") Long productId
    ) {
        cartService.removeItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear all items from a cart")
    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> clearCart(
        @Parameter(description = "Cart's ID") @PathVariable("cartId") UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of("error", "Cart not found.")
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of("error", "Product was not found.")
        );
    }
}
