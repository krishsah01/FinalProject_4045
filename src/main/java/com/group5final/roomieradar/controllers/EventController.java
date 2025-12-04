package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.EventRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.EventService;
import jakarta.persistence.Id;
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

    @GetMapping("/add")
    public String showAddEventForm(Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        model.addAttribute("event", new Event());
        return "add-event";
    }

    @PostMapping("/add")
    public String addEvent(@RequestParam String name,
                           @RequestParam String description,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }

        Optional<User> currentUserOpt = currentUserService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User currentUser = currentUserOpt.get();  // Unwrap the Optional here
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setUserid(currentUser);
        event.setHousehold(currentUser.getHousehold());
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Optional<User> currentUserOpt = currentUserService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            User currentUser = currentUserOpt.get();
//             Verify user belongs to the same household
            if (event.getHousehold().getId().equals(currentUser.getHousehold().getId())) {
                eventService.deleteEvent(id);
            }
        }
        return "redirect:/events";
    }

}
