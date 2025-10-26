package com.group5final.roomieradar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/events")
    public String events() {
        return "events";
    }

    @GetMapping("/users")
    public String users() {
        return "users";
    }
}
