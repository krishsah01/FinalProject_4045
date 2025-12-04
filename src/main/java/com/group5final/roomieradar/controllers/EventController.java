package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
