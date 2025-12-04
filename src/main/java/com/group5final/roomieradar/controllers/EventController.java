package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.EventRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final CurrentUserService currentUserService;

    public EventController(EventService eventService, CurrentUserService currentUserService) {
        this.eventService = eventService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String events(Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        model.addAttribute("events", eventService.getEventsForCurrentUserHousehold());
        return "events";
    }

    @GetMapping("/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }

        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return "redirect:/events";
        }

        model.addAttribute("event", event.get());
        return "event-details";
    }

    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable Long id, Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return "redirect:/events";
        }
        model.addAttribute("event", event.get());
        return "edit-event";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setName(name);
            event.setDescription(description);
            event.setEventDate(eventDate);
            eventService.saveEvent(event);
        }
        return "redirect:/events/" + id;
    }

}
