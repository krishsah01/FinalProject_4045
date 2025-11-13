package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Household;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HouseholdRepository extends CrudRepository<Household, Long> {
    Optional<Household> findByName(String name);
    Optional<Household> findByNameAndPassword(String name, String password);
}
