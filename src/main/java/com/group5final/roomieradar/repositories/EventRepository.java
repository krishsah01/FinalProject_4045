package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.Household;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByHouseholdOrderByEventDateAsc(Household household);
}
