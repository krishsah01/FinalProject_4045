package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // New helper: fetch all users assigned to a household
    List<User> findByHousehold_Id(Long householdId);
}
