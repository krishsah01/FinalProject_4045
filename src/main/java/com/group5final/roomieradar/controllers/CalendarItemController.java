package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.CalendarItem;
import com.group5final.roomieradar.repositories.CalendarItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendaritems")
public class CalendarItemController {
    private final CalendarItemRepository repo;
    public CalendarItemController(CalendarItemRepository repo){ this.repo = repo; }

    // 1) All items (fallback in calendar.js)
    @GetMapping
    public List<CalendarItem> getAll(
            @RequestParam(value = "householdId", required = false) Long householdId) {
        if (householdId != null) return repo.findByHouseholdId(householdId);
        return repo.findAll();
    }

    // 2) Range endpoint (primary call in calendar.js)
    @GetMapping("/range")
    public List<CalendarItem> getByRange(
            @RequestParam("start") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end")   @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(value = "householdId", required = false) Long householdId) {
        if (householdId != null) {
            return repo.findByHouseholdIdAndDateStartBetween(householdId, start, end);
        }
        // Prefer a repo method for this instead of in-memory filtering:
        return repo.findByDateStartBetween(start, end);
    }

    @PostMapping
    public ResponseEntity<CalendarItem> create(@RequestBody CalendarItem calendarItem){
        if (calendarItem.getDateStart() == null || calendarItem.getCreator() == null || calendarItem.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(calendarItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarItem> update(@PathVariable Long id,
                                               @RequestBody CalendarItem incoming) {
        Optional<CalendarItem> existing = repo.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        CalendarItem e = existing.get();
        if (incoming.getName() != null) e.setName(incoming.getName());
        e.setDescription(incoming.getDescription());
        if (incoming.getDateStart() != null) e.setDateStart(incoming.getDateStart());
        e.setDateEnd(incoming.getDateEnd());
        e.setRepeatDuration(incoming.getRepeatDuration());
        if (incoming.getCreator() != null) e.setCreator(incoming.getCreator());

        return ResponseEntity.ok(repo.save(e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
