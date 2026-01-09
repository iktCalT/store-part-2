package com.ken.store.users.repositories;

import org.springframework.data.repository.CrudRepository;
import com.ken.store.users.entities.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
}