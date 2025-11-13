package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.HouseholdMember;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdMemberRepository;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private HouseholdMemberRepository householdMemberRepository;

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

        // Get all households for dropdown
        model.addAttribute("households", householdRepository.findAll());
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        // Get all households for dropdown
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

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("households", householdRepository.findAll());
            return "signup";
        }

        Household household = null;

        // Handle household actions
        if ("join".equals(householdAction)) {
            // Join existing household
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
            // Create new household
            if (newHouseholdName == null || newHouseholdName.trim().isEmpty() ||
                newHouseholdPassword == null || newHouseholdPassword.trim().isEmpty()) {
                model.addAttribute("error", "Please provide household name and password");
                model.addAttribute("households", householdRepository.findAll());
                return "signup";
            }

            // Check if household name already exists
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

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Plain text for now
        user.setIsAdmin(false);
        user = userRepository.save(user);

        // Link user to household if one was selected/created
        if (household != null) {
            HouseholdMember member = new HouseholdMember();
            member.setHouseholdId(household.getId());
            member.setUserId(user.getId());
            householdMemberRepository.save(member);
        }

        return "redirect:/login?registered=true";
    }

    // API endpoint to get household name by ID (for frontend)
    @GetMapping("/api/household/name")
    @ResponseBody
    public String getHouseholdName(@RequestParam("id") Long id) {
        Optional<Household> household = householdRepository.findById(id);
        return household.map(Household::getName).orElse("");
    }
}

