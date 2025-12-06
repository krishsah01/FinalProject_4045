package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.HouseholdService;
import com.group5final.roomieradar.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private HouseholdRepository householdRepository;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        model.addAttribute("households", householdRepository.findAll());
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("households", householdRepository.findAll());
        model.addAttribute("userRegistrationDTO", new com.group5final.roomieradar.dto.UserRegistrationDTO());
        return "signup";
    }

    @PostMapping("/auth/signup")
    public String registerUser(@jakarta.validation.Valid @ModelAttribute("userRegistrationDTO") com.group5final.roomieradar.dto.UserRegistrationDTO registrationDTO,
                               org.springframework.validation.BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("households", householdRepository.findAll());
            return "signup";
        }

        try {
            userService.registerUser(registrationDTO);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("households", householdRepository.findAll());
            return "signup";
        }
    }

    @GetMapping("/api/household/name")
    @ResponseBody
    public String getHouseholdName(@RequestParam("id") Long id) {
        return householdRepository.findById(id).map(Household::getName).orElse("");
    }
}
