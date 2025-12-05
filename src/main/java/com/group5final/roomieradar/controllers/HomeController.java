package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CurrentUserService currentUserService;

    private boolean hasHousehold() {
        return currentUserService.hasHousehold();
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "index";
    }


    @GetMapping("/chores")
    public String chores(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "chores";
    }

    @GetMapping("/chores/add")
    public String addChore(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "add-chore";
    }

    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "events";
    }

    @GetMapping("/events/add")
    public String addEvent(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "add-event";
    }

    @GetMapping("/events/edit")
    public String editEvent(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "edit-event";
    }

    @GetMapping("/events/details")
    public String eventDetails(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "event-details";
    }

    @GetMapping("/calendar")
    public String calendar(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "calendar";
    }
}
