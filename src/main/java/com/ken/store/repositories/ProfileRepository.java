package com.ken.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.ken.store.entities.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}