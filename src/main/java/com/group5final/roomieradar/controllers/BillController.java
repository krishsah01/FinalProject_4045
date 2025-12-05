package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Bill;
import com.group5final.roomieradar.entities.BillSplit;
import com.group5final.roomieradar.entities.User;

import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.BillService;
import com.group5final.roomieradar.services.CurrentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/bills")
public class BillController {

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillService billService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping
    @Transactional(readOnly = true)
    public String bills(Model model) {
        User currentUser = currentUserService.getCurrentUser()
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        if (currentUser.getHousehold() == null) {
            model.addAttribute("noHousehold", true);
            model.addAttribute("bills", List.of());
            model.addAttribute("householdUsers", List.of());
            return "bills";
        }

        Long householdId = currentUser.getHousehold().getId();
        List<Bill> bills = billService.getBillsByUser(currentUser.getId());
        List<User> householdUsers = userRepository.findByHouseholdId(householdId);
        
        List<com.group5final.roomieradar.dto.UserDTO> userDTOs = householdUsers.stream()
            .map(user -> new com.group5final.roomieradar.dto.UserDTO(user.getId(), user.getUsername()))
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("noHousehold", false);
        model.addAttribute("bills", bills);
        model.addAttribute("householdUsers", userDTOs);
        model.addAttribute("currentUserId", currentUser.getId());

        return "bills";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addBill(
            @RequestParam String name,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String dueDate,
            @RequestParam(required = false) List<Long> userIds,
            @RequestParam(defaultValue = "false") boolean splitEqually) {

        try {
            LocalDateTime dueDateParsed = null;
            if (dueDate != null && !dueDate.isEmpty()) {
                dueDateParsed = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            billService.createBill(name, amount, description, dueDateParsed, userIds, splitEqually);
            return "success";
        } catch (Exception e) {
            logger.error("Error adding bill", e);
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/{id}/splits")
    @ResponseBody
    public List<BillSplit> getBillSplits(@PathVariable Long id) {
        return billService.getBillSplitsByBill(id);
    }

    @PostMapping("/split/{id}/mark-paid")
    @ResponseBody
    public String markAsPaid(@PathVariable Long id) {
        try {
            billService.markBillSplitAsPaid(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteBill(@PathVariable Long id) {
        try {
            billService.deleteBill(id);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}

