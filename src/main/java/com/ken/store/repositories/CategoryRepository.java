package com.ken.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.ken.store.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}