package com.ken.store.orders.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ken.store.orders.entities.Order;
import com.ken.store.users.entities.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // findBy... and findAllBy... are exactly the same
    // findByCustomer and findByCustomerId has close efficiency
    // public List<Order> findByCustomerId(Long id);
    // public List<Order> findAllByCustomer(User customer);

    @EntityGraph(attributePaths = "items.product")
    @Query("select o from Order o where o.customer = :customer")
    public List<Order> getOrdersWithItemsByCustomer(@Param("customer") User customer);

    @EntityGraph(attributePaths = "items.product")
    @Query("select o from Order o where o.id = :orderId")
    public Optional<Order> getOrderWithItem(@Param("orderId") Long orderId);

}
