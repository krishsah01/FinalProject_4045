package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.HouseholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing household-related operations.
 * <p>
 * This controller handles viewing household details, joining existing households,
 * and creating new households.
 * </p>
 */
@Controller
public class HouseholdController {

    @Autowired private UserRepository userRepository;
    @Autowired private HouseholdService householdService;
    @Autowired private CurrentUserService currentUserService;

    /**
     * Handles GET requests to view household details ("/household").
     * <p>
     * Adds current user, members, and messages to the model. Sorts members by username.
     * </p>
     *
     * @param requires optional parameter indicating if household is required
     * @param msg optional message parameter
     * @param model the model to add attributes to
     * @return the name of the view to render ("household")
     */
    @GetMapping("/household")
    public String household(@RequestParam(value = "requiresHousehold", required = false) String requires,
                            @RequestParam(value = "msg", required = false) String msg,
                            Model model) {
        User me = currentUserService.getCurrentUser().orElse(null);
        model.addAttribute("me", me);
        model.addAttribute("message", msg);
        if (requires != null) {
            model.addAttribute("warning", "Join or create a household to access chores, bills, and events.");
        }
        if (me != null && me.getHousehold() != null) {
            List<User> members = userRepository.findByHousehold_Id(me.getHousehold().getId());
            members.sort((a,b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
            model.addAttribute("members", members);
            model.addAttribute("membersCount", members.size());
        }
        return "household";
    }

    /**
     * Handles POST requests to join a household ("/household/join").
     * <p>
     * Attempts to join the specified household using the provided credentials.
     * Redirects back to household page on success or with error on failure.
     * </p>
     *
     * @param name the name of the household
     * @param password the password of the household
     * @param model the model to add attributes to (for errors)
     * @return redirect to "/household" on success, or household view on error
     */
    @PostMapping("/household/join")
    public String joinHousehold(@RequestParam("name") String name,
                                @RequestParam("password") String password,
                                Model model) {
        User me = currentUserService.getCurrentUser().orElse(null);
        if (me == null) return "redirect:/login";
        
        try {
            householdService.joinHousehold(name, password, me);
            return "redirect:/household?msg=Joined";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("me", me);
            return household(null, null, model);
        }
    }

    /**
     * Handles POST requests to create a new household ("/household/create").
     * <p>
     * Validates input and creates the household, assigning the current user.
     * Redirects back to household page on success or with error on failure.
     * </p>
     *
     * @param name the name for the new household
     * @param password the password for the new household
     * @param model the model to add attributes to (for errors)
     * @return redirect to "/household" on success, or household view on error
     */
    @PostMapping("/household/create")
    public String createHousehold(@RequestParam("name") String name,
                                  @RequestParam("password") String password,
                                  Model model) {
        User me = currentUserService.getCurrentUser().orElse(null);
        if (me == null) return "redirect:/login";
        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Household name and password are required");
            model.addAttribute("me", me);
            return household(null, null, model);
        }
        
        try {
            householdService.createHousehold(name, password, me);
            return "redirect:/household?msg=Created";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("me", me);
            return household(null, null, model);
        }
    }
}
