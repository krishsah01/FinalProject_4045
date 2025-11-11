package com.group5final.roomieradar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/bills")
    public String bills() {
        return "bills";
    }

    @GetMapping("/bills/add")
    public String addBill() {
        return "add-bill";
    }

    @GetMapping("/chores")
    public String chores() {
        return "chores";
    }

    @GetMapping("/chores/add")
    public String addChore() {
        return "add-chore";
    }

    @GetMapping("/calendar")
    public String calendar() {
        return "calendar";
    }

    @GetMapping("/users")
    public String users() {
        return "users";
    }
}
