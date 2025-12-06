package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Chore;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.ChoreRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Controller
public class ChoreController {

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping("/chores")
    public String showChores(Model model,
                             @RequestParam(value = "msg", required = false) String msg) {
        User currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));

        Household household = currentUser.getHousehold();
        if (household == null) {
            model.addAttribute("noHousehold", true);
            return "chores";
        }

        Iterable<Chore> chores = choreRepository.findByHouseholdId(household.getId());
        model.addAttribute("chores", chores);
        model.addAttribute("noHousehold", false);
        model.addAttribute("msg", msg);

        return "chores";
    }

    @GetMapping("/chores/add")
    public String showAddChoreForm(Model model,
                                   @RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "msg", required = false) String msg) {

        User currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));

        Household household = currentUser.getHousehold();
        if (household == null) {
            return "redirect:/household?requiresHousehold=true";
        }

        List<User> members = userRepository.findByHousehold_Id(household.getId());
        members.sort((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));

        model.addAttribute("members", members);
        model.addAttribute("error", error);
        model.addAttribute("msg", msg);

        return "add-chore";
    }

    @PostMapping("/chores/add")
    public String addChore(@RequestParam("name") String name,
                           @RequestParam("assigneeId") Long assigneeId,
                           @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                           @RequestParam(value = "description", required = false) String description) {

        User currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not logged in"));

        Household household = currentUser.getHousehold();
        if (household == null) {
            return "redirect:/household?requiresHousehold=true";
        }

        Optional<User> assigneeOpt = userRepository.findById(assigneeId);
        if (assigneeOpt.isEmpty() || !assigneeOpt.get().getHousehold().getId().equals(household.getId())) {
            return "redirect:/chores/add?error=Invalid assignee selected";
        }

        Chore chore = new Chore();
        chore.setName(name);
        chore.setUser(assigneeOpt.get());
        chore.setHousehold(household);
        chore.setDueDate(dueDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        chore.setDescription(description);

        choreRepository.save(chore);

        return "redirect:/chores?msg=Chore added successfully!";
    }
}
