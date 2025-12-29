package com.ken.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.ken.store.entities.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
}