package com.ken.store.carts.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import com.ken.store.products.entities.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // UUID
    @Column(name = "id")
    private UUID id;

    // make sure hibernate won't change it when updating carts
    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(cartItem -> cartItem.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItem getItemById(Long productId) {
        return this.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public CartItem addItem(Product product) {
        var cartItem = getItemById(product.getId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(this);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            items.add(cartItem);
        }

        return cartItem;
    }

    public void removeItemById(Long productId) {
        var cartItem = getItemById(productId);
        if (cartItem != null) {
            items.remove(cartItem);
        }
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}