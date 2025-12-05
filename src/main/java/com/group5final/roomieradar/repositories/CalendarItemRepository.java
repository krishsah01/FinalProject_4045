package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.CalendarItem;
import com.group5final.roomieradar.entities.Household;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CalendarItemRepository extends CrudRepository<CalendarItem, Long> {
    List<CalendarItem> findByHousehold(Household household);
}
