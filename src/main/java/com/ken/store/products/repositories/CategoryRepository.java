package com.ken.store.products.repositories;

import org.springframework.data.repository.CrudRepository;
import com.ken.store.products.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}