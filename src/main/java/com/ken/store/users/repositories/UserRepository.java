package com.ken.store.users.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ken.store.users.entities.User;

// CrudRepository.findAll() returns an Iterable
// JpaRepository.findAll() returns a List
public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmail(String email);

    public Optional<User> findByEmail(String email);
}
