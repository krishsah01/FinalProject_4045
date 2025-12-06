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
    public String addChore() { return hasHousehold() ? "add-chore" : "redirect:/household?requiresHousehold=true"; }
    
    // Events and Calendar routes are now handled by their own controllers
}
