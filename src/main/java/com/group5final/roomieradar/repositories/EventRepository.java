package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
    Iterable<Event> findByHousehold_Id(Long householdId);
}
