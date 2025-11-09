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
    public CalendarItemController(CalendarItemRepository repo) {this.repo = repo;}

    @GetMapping
    public List<CalendarItem> list(
            @RequestParam(required = false) Long householdId,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end
    ){
        if (householdId != null && start != null && end != null) {
            return repo.findByHouseholdIdAndDateStartBetween(householdId, start, end);
        }
        if (householdId != null) return repo.findByHouseholdId(householdId);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarItem> get(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<CalendarItem> create(@RequestBody CalendarItem calendarItem){
        if (calendarItem.getDateStart() == null || calendarItem.getCreator() == null || calendarItem.getId() == null || calendarItem.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(calendarItem));
    }

    @PutMapping
    public ResponseEntity<CalendarItem> update(@PathVariable Long id, @RequestBody CalendarItem incoming) {
        Optional<CalendarItem> existing = repo.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        CalendarItem e = existing.get();
        if (incoming.getName() != null) e.setName(incoming.getName());
        e.setDescription(incoming.getDescription());
        if (incoming.getDateStart() != null) e.setDateStart(incoming.getDateStart());
        e.setDateEnd(incoming.getDateEnd());
        e.setRepeatDuration(incoming.getRepeatDuration());
        if (incoming.getCreator() != null) e.setCreator(incoming.getCreator());
        if (incoming.getId() != null) e.setId(incoming.getId());
        return ResponseEntity.ok(repo.save(e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
