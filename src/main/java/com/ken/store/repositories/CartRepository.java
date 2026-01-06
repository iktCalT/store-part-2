package com.ken.store.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ken.store.entities.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    // attributePaths = "items.product"
    // "items": Set<CartItem> items of Cart
    // "product": Product product of each CartItem
    @EntityGraph(attributePaths = "items.product")
    @Query("SELECT c FROM Cart c WHERE id = :cartId")
    Optional<Cart> getCartWithItems(@Param("cartId") UUID cartId);
    
}
