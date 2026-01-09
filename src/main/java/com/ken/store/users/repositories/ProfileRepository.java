package com.ken.store.users.repositories;

import org.springframework.data.repository.CrudRepository;
import com.ken.store.users.entities.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}