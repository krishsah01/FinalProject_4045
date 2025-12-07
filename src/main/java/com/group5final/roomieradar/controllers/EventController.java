package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.EventRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.EventService;
import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller for managing event-related operations.
 * <p>
 * This controller handles listing, viewing, editing, adding, and deleting events
 * within a household context.
 * </p>
 */
@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final CurrentUserService currentUserService;
    private final com.group5final.roomieradar.repositories.UserRepository userRepository;

    /**
     * Constructs the EventController with required services and repositories.
     *
     * @param eventService service for event operations
     * @param currentUserService service for current user information
     * @param userRepository repository for user data
     */
    public EventController(EventService eventService, CurrentUserService currentUserService, com.group5final.roomieradar.repositories.UserRepository userRepository) {
        this.eventService = eventService;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    /**
     * Handles GET requests to list events ("/events").
     * <p>
     * Redirects if no household; otherwise, adds events to the model.
     * </p>
     *
     * @param model the model to add attributes to
     * @return the name of the view to render ("events") or redirect
     */
    @GetMapping
    public String events(Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        model.addAttribute("events", eventService.getEventsForCurrentUserHousehold());
        model.addAttribute("noHousehold", false);
        return "events";
    }

    /**
     * Handles GET requests to view event details ("/events/{id}").
     * <p>
     * Redirects if no household or event not found; otherwise, adds event to the model.
     * </p>
     *
     * @param id the ID of the event
     * @param model the model to add attributes to
     * @return the name of the view to render ("event-details") or redirect
     */
    @GetMapping("/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }

        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return "redirect:/events";
        }

        model.addAttribute("event", event.get());
        model.addAttribute("noHousehold", false);
        return "event-details";
    }

    /**
     * Handles GET requests to edit an event ("/events/edit/{id}").
     * <p>
     * Redirects if no household or event not found; otherwise, prepares the edit form.
     * </p>
     *
     * @param id the ID of the event
     * @param model the model to add attributes to
     * @return the name of the view to render ("edit-event") or redirect
     */
    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable Long id, Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        Optional<Event> event = eventService.getEventById(id);
        if (event.isEmpty()) {
            return "redirect:/events";
        }
        model.addAttribute("event", event.get());
        model.addAttribute("noHousehold", false);
        model.addAttribute("householdMembers", userRepository.findByHouseholdId(currentUserService.getCurrentUser().get().getHousehold().getId()));
        return "edit-event";
    }

    /**
     * Handles POST requests to update an event ("/events/edit/{id}").
     * <p>
     * Updates event details and attendees if the event exists.
     * </p>
     *
     * @param id the ID of the event
     * @param name the updated name
     * @param description the updated description
     * @param eventDate the updated event date
     * @param attendeeIds optional list of attendee IDs
     * @return redirect to event details
     */
    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate,
                              @RequestParam(required = false) java.util.List<Long> attendeeIds) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setName(name);
            event.setDescription(description);
            event.setEventDate(eventDate);
            if (attendeeIds != null) {
                java.util.List<User> attendees = (java.util.List<User>) userRepository.findAllById(attendeeIds);
                event.setAttendees(new java.util.HashSet<>(attendees));
            } else {
                event.getAttendees().clear();
            }
            eventService.saveEvent(event);
        }
        return "redirect:/events/" + id;
    }

    /**
     * Handles GET requests to show the add event form ("/events/add").
     * <p>
     * Redirects if no household; otherwise, prepares the form with household members.
     * </p>
     *
     * @param model the model to add attributes to
     * @return the name of the view to render ("add-event") or redirect
     */
    @GetMapping("/add")
    public String showAddEventForm(Model model) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }
        model.addAttribute("event", new Event());
        model.addAttribute("noHousehold", false);
        model.addAttribute("householdMembers", userRepository.findByHouseholdId(currentUserService.getCurrentUser().get().getHousehold().getId()));
        return "add-event";
    }

    /**
     * Handles POST requests to add a new event ("/events/add").
     * <p>
     * Creates and saves a new event with the provided details and attendees.
     * </p>
     *
     * @param name the name of the event
     * @param description the description
     * @param eventDate the event date
     * @param attendeeIds optional list of attendee IDs
     * @return redirect to events list or login if not authenticated
     */
    @PostMapping("/add")
    public String addEvent(@RequestParam String name,
                           @RequestParam String description,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate,
                           @RequestParam(required = false) java.util.List<Long> attendeeIds) {
        if (!currentUserService.hasHousehold()) {
            return "redirect:/household?requiresHousehold=true";
        }

        Optional<User> currentUserOpt = currentUserService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User currentUser = currentUserOpt.get();  // Unwrap the Optional here
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setUserid(currentUser);
        event.setHousehold(currentUser.getHousehold());
        if (attendeeIds != null) {
            java.util.List<User> attendees = (java.util.List<User>) userRepository.findAllById(attendeeIds);
            event.setAttendees(new java.util.HashSet<>(attendees));
        }
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    /**
     * Handles GET requests to delete an event ("/events/delete/{id}").
     * <p>
     * Deletes the event if the current user is in the same household.
     * </p>
     *
     * @param id the ID of the event
     * @return redirect to events list or login if not authenticated
     */
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Optional<User> currentUserOpt = currentUserService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            User currentUser = currentUserOpt.get();
//             Verify user belongs to the same household
            if (event.getHousehold().getId().equals(currentUser.getHousehold().getId())) {
                eventService.deleteEvent(id);
            }
        }
        return "redirect:/events";
    }

}
