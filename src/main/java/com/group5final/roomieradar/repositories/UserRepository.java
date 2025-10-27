package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
