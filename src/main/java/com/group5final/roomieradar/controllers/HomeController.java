package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling home page requests.
 * <p>
 * This controller manages the root endpoint and checks for household membership
 * to customize the user experience.
 * </p>
 */
@Controller
public class HomeController {

    @Autowired
    private CurrentUserService currentUserService;

    /**
     * Checks if the current user has a household.
     *
     * @return true if the user has a household, false otherwise
     */
    private boolean hasHousehold() {
        return currentUserService.hasHousehold();
    }

    /**
     * Handles GET requests to the root path ("/").
     * <p>
     * Adds an attribute to the model indicating whether the user has a household.
     * </p>
     *
     * @param model the model to add attributes to
     * @return the name of the view to render ("index")
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("noHousehold", !hasHousehold());
        return "index";
    }
}
