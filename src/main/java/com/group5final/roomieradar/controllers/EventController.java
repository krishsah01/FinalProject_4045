package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.repositories.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class EventController {
    private EventRepository eventRepository;

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "events";
    }

    @GetMapping("/events/add")
    public String addEvent() {
        return "add-event";
    }

    @GetMapping("/events/edit")
    public String editEvent() {
        return "edit-event";
    }

    @GetMapping("/events/details")
    public String eventDetails() {
        return "event-details";
    }
}
