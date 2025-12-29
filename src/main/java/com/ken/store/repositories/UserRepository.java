package com.ken.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ken.store.entities.User;

// CrudRepository.findAll() returns an Iterable
// JpaRepository.findAll() returns a List
public interface UserRepository extends JpaRepository<User, Long> {
}
