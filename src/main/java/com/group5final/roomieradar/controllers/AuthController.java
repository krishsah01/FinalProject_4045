package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

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
        return "signup";
    }

    @PostMapping("/auth/signup")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("householdAction") String householdAction,
                               @RequestParam(value = "householdId", required = false) Long householdId,
                               @RequestParam(value = "householdName", required = false) String householdName,
                               @RequestParam(value = "householdPassword", required = false) String householdPassword,
                               @RequestParam(value = "newHouseholdName", required = false) String newHouseholdName,
                               @RequestParam(value = "newHouseholdPassword", required = false) String newHouseholdPassword,
                               Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("households", householdRepository.findAll());
            return "signup";
        }
        Household household = null;
        if ("join".equals(householdAction)) {
            if (householdId == null || householdName == null || householdPassword == null) {
                model.addAttribute("error", "Please select a household and enter the password");
                model.addAttribute("households", householdRepository.findAll());
                return "signup";
            }
            Optional<Household> householdOpt = householdRepository.findByNameAndPassword(householdName, householdPassword);
            if (householdOpt.isEmpty()) {
                model.addAttribute("error", "Invalid household password");
                model.addAttribute("households", householdRepository.findAll());
                return "signup";
            }
            household = householdOpt.get();
        } else if ("create".equals(householdAction)) {
            if (newHouseholdName == null || newHouseholdName.trim().isEmpty() ||
                newHouseholdPassword == null || newHouseholdPassword.trim().isEmpty()) {
                model.addAttribute("error", "Please provide household name and password");
                model.addAttribute("households", householdRepository.findAll());
                return "signup";
            }
            if (householdRepository.findByName(newHouseholdName).isPresent()) {
                model.addAttribute("error", "Household name already exists");
                model.addAttribute("households", householdRepository.findAll());
                return "signup";
            }
            household = new Household();
            household.setName(newHouseholdName);
            household.setPassword(newHouseholdPassword);
            household = householdRepository.save(household);
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        if (household != null) {
            user.setHousehold(household);
        }
        userRepository.save(user);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/api/household/name")
    @ResponseBody
    public String getHouseholdName(@RequestParam("id") Long id) {
        return householdRepository.findById(id).map(Household::getName).orElse("");
    }
}
