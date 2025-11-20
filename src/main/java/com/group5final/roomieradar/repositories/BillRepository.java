package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Bill;
import org.springframework.data.repository.CrudRepository;

public interface BillRepository extends CrudRepository<Bill, Long> {
    Iterable<Bill> findByHouseholdId(Long householdId);
}
