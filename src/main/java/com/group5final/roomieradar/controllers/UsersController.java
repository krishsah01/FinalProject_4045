package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.HouseholdMember;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdMemberRepository;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private HouseholdMemberRepository householdMemberRepository;

    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "householdId", required = false) Long householdId,
                            @RequestParam(value = "msg", required = false) String msg,
                            Model model) {
        // Determine current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            return "redirect:/login";
        }
        Optional<User> meOpt = userRepository.findByUsername(username);
        if (meOpt.isEmpty()) {
            return "redirect:/login";
        }
        User me = meOpt.get();

        // Resolve default household: param -> first membership -> user's legacy household field
        List<HouseholdMember> myMemberships = householdMemberRepository.findByUserId(me.getId());
        if (householdId == null) {
            if (!myMemberships.isEmpty()) {
                householdId = myMemberships.get(0).getHouseholdId();
            } else if (me.getHousehold() != null) {
                householdId = me.getHousehold().getId();
            }
        }

        List<User> members = new ArrayList<>();
        Household currentHousehold = null;
        if (householdId != null) {
            currentHousehold = householdRepository.findById(householdId).orElse(null);
            if (currentHousehold != null) {
                List<HouseholdMember> links = householdMemberRepository.findByHouseholdId(householdId);
                // Prefer membership mapping; if empty, fall back to legacy mapping
                if (!links.isEmpty()) {
                    members = links.stream()
                            .map(HouseholdMember::getUser)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                } else {
                    // legacy: users with householdId column
                    Iterable<User> all = userRepository.findAll();
                    for (User u : all) {
                        if (u.getHousehold() != null && Objects.equals(u.getHousehold().getId(), householdId)) {
                            members.add(u);
                        }
                    }
                }
            }
        }

        // Households the current user belongs to (for switcher)
        List<Long> myHouseholdIds = myMemberships.stream()
                .map(HouseholdMember::getHouseholdId)
                .distinct().toList();
        List<Household> myHouseholds = new ArrayList<>();
        for (Long hid : myHouseholdIds) {
            householdRepository.findById(hid).ifPresent(myHouseholds::add);
        }

        model.addAttribute("members", members);
        model.addAttribute("currentHousehold", currentHousehold);
        model.addAttribute("myHouseholds", myHouseholds);
        model.addAttribute("message", msg);
        return "users";
    }

    @GetMapping("/users/add")
    public String addUserForm(@RequestParam(value = "householdId", required = false) Long householdId,
                              Model model) {
        // If no household provided, attempt to infer like listUsers
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> meOpt = username != null ? userRepository.findByUsername(username) : Optional.empty();
        if (householdId == null && meOpt.isPresent()) {
            User me = meOpt.get();
            List<HouseholdMember> memberships = householdMemberRepository.findByUserId(me.getId());
            if (!memberships.isEmpty()) {
                householdId = memberships.get(0).getHouseholdId();
            } else if (me.getHousehold() != null) {
                householdId = me.getHousehold().getId();
            }
        }
        Household h = null;
        if (householdId != null) {
            h = householdRepository.findById(householdId).orElse(null);
        }
        model.addAttribute("household", h);
        return "add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam("username") String newUsername,
                          @RequestParam(value = "email", required = false) String email,
                          @RequestParam("householdId") Long householdId,
                          Model model) {
        // Validate target household
        Household household = householdRepository.findById(householdId).orElse(null);
        if (household == null) {
            model.addAttribute("error", "Selected household not found");
            return "add-user";
        }

        // Find existing user by username
        Optional<User> existingOpt = userRepository.findByUsername(newUsername);
        User user;
        if (existingOpt.isPresent()) {
            user = existingOpt.get();
        } else {
            // Creating a new user: username must be unique (enforced here)
            // Generate a temporary password since DB column is NOT NULL but we don't require input
            String generatedPassword = "changeme" + UUID.randomUUID().toString().substring(0, 6);
            user = new User();
            user.setUsername(newUsername);
            user.setEmail(email != null ? email : newUsername + "@example.com");
            user.setPassword(generatedPassword);
            // Remove admin logic: treat every user as admin the same; set true to neutralize any checks
            user.setIsAdmin(true);
            user = userRepository.save(user);
        }

        // Prevent duplicate membership links
        final Long userId = user.getId();
        boolean alreadyMember = householdMemberRepository.findByHouseholdId(householdId).stream()
                .anyMatch(hm -> Objects.equals(hm.getUserId(), userId));
        if (!alreadyMember) {
            HouseholdMember link = new HouseholdMember();
            link.setHouseholdId(householdId);
            link.setUserId(userId);
            householdMemberRepository.save(link);
            return "redirect:/users?householdId=" + householdId + "&msg=User%20added";
        } else {
            return "redirect:/users?householdId=" + householdId + "&msg=User%20already%20in%20household";
        }
    }
}
