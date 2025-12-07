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

/**
 * Controller for handling authentication-related requests.
 * <p>
 * This controller manages login, signup, and user registration processes,
 * including error handling and household selection.
 * </p>
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private HouseholdRepository householdRepository;

    /**
     * Handles GET requests to the login page ("/login").
     * <p>
     * Displays error or logout messages if provided as query parameters.
     * Adds a list of all households to the model for display.
     * </p>
     *
     * @param error optional error parameter indicating login failure
     * @param logout optional logout parameter indicating successful logout
     * @param model the model to add attributes to
     * @return the name of the view to render ("login")
     */
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

    /**
     * Handles GET requests to the signup page ("/signup").
     * <p>
     * Prepares the model with a list of households and a new user registration DTO.
     * </p>
     *
     * @param model the model to add attributes to
     * @return the name of the view to render ("signup")
     */
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("households", householdRepository.findAll());
        model.addAttribute("userRegistrationDTO", new com.group5final.roomieradar.dto.UserRegistrationDTO());
        return "signup";
    }

    /**
     * Handles POST requests for user registration ("/auth/signup").
     * <p>
     * Validates the registration DTO and attempts to register the user.
     * Redirects to login on success or back to signup on validation errors or exceptions.
     * </p>
     *
     * @param registrationDTO the user registration data
     * @param bindingResult the result of validation
     * @param model the model to add attributes to (for errors)
     * @return redirect to login on success, or "signup" view on failure
     */
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

    /**
     * Handles GET requests to retrieve a household name by ID ("/api/household/name").
     * <p>
     * This is an API endpoint returning the name as plain text.
     * </p>
     *
     * @param id the ID of the household
     * @return the name of the household, or an empty string if not found
     */
    @GetMapping("/api/household/name")
    @ResponseBody
    public String getHouseholdName(@RequestParam("id") Long id) {
        return householdRepository.findById(id).map(Household::getName).orElse("");
    }
}
