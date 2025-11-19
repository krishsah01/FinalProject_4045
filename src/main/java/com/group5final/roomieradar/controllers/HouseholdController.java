package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class HouseholdController {

    @Autowired private UserRepository userRepository;
    @Autowired private HouseholdRepository householdRepository;
    @Autowired private CurrentUserService currentUserService;

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

    @PostMapping("/household/join")
    public String joinHousehold(@RequestParam("name") String name,
                                @RequestParam("password") String password,
                                Model model) {
        User me = currentUserService.getCurrentUser().orElse(null);
        if (me == null) return "redirect:/login";
        Optional<Household> h = householdRepository.findByNameAndPassword(name, password);
        if (h.isEmpty()) {
            model.addAttribute("error", "Invalid household name or password");
            model.addAttribute("me", me);
            return household(null, null, model); // reuse listing logic
        }
        me.setHousehold(h.get());
        userRepository.save(me);
        return "redirect:/household?msg=Joined";
    }

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
        if (householdRepository.findByName(name).isPresent()) {
            model.addAttribute("error", "Household name already exists");
            model.addAttribute("me", me);
            return household(null, null, model);
        }
        Household h = new Household();
        h.setName(name);
        h.setPassword(password);
        h = householdRepository.save(h);
        me.setHousehold(h);
        userRepository.save(me);
        return "redirect:/household?msg=Created";
    }
}
