package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.CalendarItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CalendarItemRepository extends JpaRepository<CalendarItem, Long> {
    List<CalendarItem> findByHouseholdId(Long householdId);

    List<CalendarItem> findByDateStartBetween(Instant start, Instant end);

    List<CalendarItem> findByHouseholdIdAndDateStartBetween(Long householdId, Instant start, Instant end);
}
