package com.ken.store.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ken.store.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public Optional<Order> findById(Long id);
    public List<Order> findByCustomerId(Long id);
}
