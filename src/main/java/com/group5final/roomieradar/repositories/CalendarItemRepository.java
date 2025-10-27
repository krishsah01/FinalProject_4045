package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.CalendarItem;
import org.springframework.data.repository.CrudRepository;

public interface CalendarItemRepository extends CrudRepository<CalendarItem, Long> {
}
