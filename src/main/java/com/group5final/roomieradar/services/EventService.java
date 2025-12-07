package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final CurrentUserService currentUserService;

    public EventService(EventRepository eventRepository, CurrentUserService currentUserService) {
        this.eventRepository = eventRepository;
        this.currentUserService = currentUserService;
    }

    public List<Event> getEventsForCurrentUserHousehold() {
        return currentUserService.getCurrentUser()
                .map(User::getHousehold)
                .map(eventRepository::findByHouseholdOrderByEventDateAsc)
                .orElse(Collections.emptyList());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public void saveEvent(Event event) {
        log.info("Saving event: {}", event.getName());
        eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        log.info("Deleting event with ID: {}", id);
        eventRepository.deleteById(id);
    }

}
