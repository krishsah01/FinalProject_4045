package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Chore;
import org.springframework.data.repository.CrudRepository;

public interface ChoreRepository extends CrudRepository<Chore, Long> {
    Iterable<Chore> findByHousehold_Id(Long householdId);
}
