package com.ken.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ken.store.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}