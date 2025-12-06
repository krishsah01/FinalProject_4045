// java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.dto.CalendarItemInstance;
import com.group5final.roomieradar.entities.CalendarItem;
import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.CalendarItemRepository;
import com.group5final.roomieradar.repositories.EventRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final EventRepository eventRepository;
    private final CalendarItemRepository calendarItemRepository;
    private final CurrentUserService currentUserService;

    public CalendarController(EventRepository eventRepository,
                              CalendarItemRepository calendarItemRepository,
                              CurrentUserService currentUserService) {
        this.eventRepository = eventRepository;
        this.calendarItemRepository = calendarItemRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String showCalendar(@RequestParam(value = "date", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {

        Optional<User> cur = currentUserService.getCurrentUser();
        if (cur.isEmpty() || cur.get().getHousehold() == null) {
            model.addAttribute("events", Collections.emptyList());
            model.addAttribute("calendarItems", Collections.emptyList());
            model.addAttribute("calendarItemInstances", Collections.emptyMap());
            model.addAttribute("noHousehold", true);
            return "calendar";
        }

        Household household = cur.get().getHousehold();
        List<Event> events = eventRepository.findByHouseholdOrderByEventDateAsc(household);
        List<CalendarItem> calendarItems = calendarItemRepository.findByHousehold(household);

        //Expand each CalendarItem into one instance per date it spans (inclusive)
        Map<String, List<CalendarItemInstance>> instancesByDate = new HashMap<>();
        for (CalendarItem ci : calendarItems) {
            if (ci.getDateStart() == null) continue;
            LocalDate start = ci.getDateStart().toLocalDate();
            LocalDate end = ci.getDateEnd() != null ? ci.getDateEnd().toLocalDate() : start;
            if (end.isBefore(start)) end = start;

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                boolean startsOnDate = d.equals(start);
                boolean endsOnDate = d.equals(end);
                CalendarItemInstance inst = new CalendarItemInstance(ci, d, startsOnDate, endsOnDate);
                String key = d.toString();
                instancesByDate.computeIfAbsent(key, k -> new ArrayList<>()).add(inst);
            }
        }


        model.addAttribute("events", events);
        model.addAttribute("calendarItems", calendarItems); // keep for backward compatibility if needed
        model.addAttribute("calendarItemInstances", instancesByDate);
        model.addAttribute("focusedDate", date != null ? date.toString() : null);
        model.addAttribute("noHousehold", false);

        return "calendar";
    }

    @GetMapping("/new")
    public String newCalendarItem(@RequestParam(value = "date", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  Model model) {
        CalendarItem calendar = new CalendarItem();
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            calendar.setDateStart(start);
        }
        model.addAttribute("calendar", calendar);
        return "edit-calendar";
    }

    @GetMapping("/{id}")
    public String editCalendarItem(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<CalendarItem> opt = calendarItemRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Calendar item not found");
            return "redirect:/calendar";
        }
        model.addAttribute("calendar", opt.get());
        return "edit-calendar";
    }

    @PostMapping
    public String createCalendarItem(@ModelAttribute("calendar") @Valid CalendarItem calendar,
                                     BindingResult br,
                                     RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "edit-calendar";
        }

        Optional<User> cur = currentUserService.getCurrentUser();
        if (cur.isEmpty() || cur.get().getHousehold() == null) {
            ra.addFlashAttribute("error", "No household available to attach item to");
            return "redirect:/calendar";
        }

        User currentUser = cur.get();
        calendar.setHousehold(currentUser.getHousehold());
        calendar.setCreator(currentUser);
        calendarItemRepository.save(calendar);
        ra.addFlashAttribute("message", "Calendar item created");
        return "redirect:/calendar";
    }

    @PostMapping(path = "/{id}", params = "_method=put")
    public String updateCalendarItem(@PathVariable("id") Long id,
                                     @ModelAttribute("calendar") @Valid CalendarItem form,
                                     BindingResult br,
                                     RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "edit-calendar";
        }

        Optional<CalendarItem> opt = calendarItemRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Calendar item not found");
            return "redirect:/calendar";
        }

        CalendarItem existing = opt.get();
        existing.setName(form.getName());
        existing.setDescription(form.getDescription());
        existing.setDateStart(form.getDateStart());
        existing.setDateEnd(form.getDateEnd());
        existing.setRepeatDuration(form.getRepeatDuration());
        // household and creator kept unchanged
        calendarItemRepository.save(existing);

        ra.addFlashAttribute("message", "Calendar item updated");
        return "redirect:/calendar";
    }

    @PostMapping(path = "/{id}", params = "_method=delete")
    public String deleteCalendarItem(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (!calendarItemRepository.existsById(id)) {
            ra.addFlashAttribute("error", "Calendar item not found");
            return "redirect:/calendar";
        }
        calendarItemRepository.deleteById(id);
        ra.addFlashAttribute("message", "Calendar item deleted");
        return "redirect:/calendar";
    }
}
