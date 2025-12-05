package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CurrentUserService currentUserService;

    private boolean hasHousehold() {
        return currentUserService.hasHousehold();
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/bills")
    public String bills() { return hasHousehold() ? "bills" : "redirect:/household?requiresHousehold=true"; }
    @GetMapping("/bills/add")
    public String addBill() { return hasHousehold() ? "add-bill" : "redirect:/household?requiresHousehold=true"; }
    @GetMapping("/chores")
    public String chores() { return hasHousehold() ? "chores" : "redirect:/household?requiresHousehold=true"; }
    @GetMapping("/chores/add")
    public String addChore() { return hasHousehold() ? "add-chore" : "redirect:/household?requiresHousehold=true"; }
//    @GetMapping("/events")
//    public String events() { return hasHousehold() ? "events" : "redirect:/household?requiresHousehold=true"; }
//    @GetMapping("/events/add")
//    public String addEvent() { return hasHousehold() ? "add-event" : "redirect:/household?requiresHousehold=true"; }
//    @GetMapping("/events/edit")
//    public String editEvent() { return hasHousehold() ? "edit-event" : "redirect:/household?requiresHousehold=true"; }
//    @GetMapping("/events/details")
//    public String eventDetails() { return hasHousehold() ? "event-details" : "redirect:/household?requiresHousehold=true"; }
//    @GetMapping("/calendar")
//    public String calendar() { return hasHousehold() ? "calendar" : "redirect:/household?requiresHousehold=true"; }
}
