// java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private EventController eventController;

    private Event sampleEvent;
    private User sampleUser;
    private Household sampleHousehold;

    @BeforeEach
    void setUp() {
        sampleHousehold = new Household();
        sampleHousehold.setId(1L);
        sampleHousehold.setName("HouseA");
        sampleHousehold.setPassword("hpw");

        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setUsername("tester");
        sampleUser.setHousehold(sampleHousehold);

        sampleEvent = new Event();
        sampleEvent.setId(5L);
        sampleEvent.setName("Party");
        sampleEvent.setDescription("desc");
        sampleEvent.setEventDate(LocalDateTime.now().plusDays(1));
        sampleEvent.setUserid(sampleUser);
        sampleEvent.setHousehold(sampleHousehold);
    }

    @Test
    void events_noHousehold_redirectsToHousehold() {
        when(currentUserService.hasHousehold()).thenReturn(false);

        String view = eventController.events(model);

        assertEquals("redirect:/household?requiresHousehold=true", view);
        verify(eventService, never()).getEventsForCurrentUserHousehold();
    }

    @Test
    void events_withHousehold_addsEventsAndReturnsView() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        List<Event> events = Collections.singletonList(sampleEvent);
        when(eventService.getEventsForCurrentUserHousehold()).thenReturn(events);

        String view = eventController.events(model);

        assertEquals("events", view);
        verify(model).addAttribute("events", events);
    }

    @Test
    void eventDetails_noHousehold_redirectsToHousehold() {
        when(currentUserService.hasHousehold()).thenReturn(false);

        String view = eventController.eventDetails(5L, model);

        assertEquals("redirect:/household?requiresHousehold=true", view);
    }

    @Test
    void eventDetails_notFound_redirectsToEvents() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        String view = eventController.eventDetails(99L, model);

        assertEquals("redirect:/events", view);
    }

    @Test
    void eventDetails_found_addsEventAndReturnsDetails() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(eventService.getEventById(5L)).thenReturn(Optional.of(sampleEvent));

        String view = eventController.eventDetails(5L, model);

        assertEquals("event-details", view);
        verify(model).addAttribute("event", sampleEvent);
    }

    @Test
    void editEvent_noHousehold_redirectsToHousehold() {
        when(currentUserService.hasHousehold()).thenReturn(false);

        String view = eventController.editEvent(5L, model);

        assertEquals("redirect:/household?requiresHousehold=true", view);
    }

    @Test
    void editEvent_notFound_redirectsToEvents() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(eventService.getEventById(5L)).thenReturn(Optional.empty());

        String view = eventController.editEvent(5L, model);

        assertEquals("redirect:/events", view);
    }

    @Test
    void editEvent_found_addsEventAndReturnsEdit() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(eventService.getEventById(5L)).thenReturn(Optional.of(sampleEvent));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        when(userRepository.findByHouseholdId(1L)).thenReturn(Collections.singletonList(sampleUser));

        String view = eventController.editEvent(5L, model);

        assertEquals("edit-event", view);
        verify(model).addAttribute("event", sampleEvent);
        verify(model).addAttribute("householdMembers", Collections.singletonList(sampleUser));
    }

    @Test
    void updateEvent_eventExists_updatesAndSaves() {
        when(eventService.getEventById(5L)).thenReturn(Optional.of(sampleEvent));
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);

        String view = eventController.updateEvent(5L, "NewName", "NewDesc", newDate, null);

        assertEquals("redirect:/events/5", view);
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).saveEvent(captor.capture());
        Event saved = captor.getValue();
        assertEquals("NewName", saved.getName());
        assertEquals("NewDesc", saved.getDescription());
        assertEquals(newDate, saved.getEventDate());
    }

    @Test
    void updateEvent_eventMissing_redirectsButDoesNotSave() {
        when(eventService.getEventById(5L)).thenReturn(Optional.empty());
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);

        String view = eventController.updateEvent(5L, "Name", "Desc", newDate, null);

        assertEquals("redirect:/events/5", view);
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    void showAddEventForm_noHousehold_redirectsToHousehold() {
        when(currentUserService.hasHousehold()).thenReturn(false);

        String view = eventController.showAddEventForm(model);

        assertEquals("redirect:/household?requiresHousehold=true", view);
    }

    @Test
    void showAddEventForm_withHousehold_addsEmptyEventAndReturnsView() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        when(userRepository.findByHouseholdId(1L)).thenReturn(Collections.singletonList(sampleUser));

        String view = eventController.showAddEventForm(model);

        assertEquals("add-event", view);
        verify(model).addAttribute(eq("event"), any(Event.class));
        verify(model).addAttribute("householdMembers", Collections.singletonList(sampleUser));
    }

    @Test
    void addEvent_noHousehold_redirectsToHousehold() {
        when(currentUserService.hasHousehold()).thenReturn(false);

        String view = eventController.addEvent("n", "d", LocalDateTime.now(), null);

        assertEquals("redirect:/household?requiresHousehold=true", view);
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    void addEvent_noCurrentUser_redirectsToLogin() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        String view = eventController.addEvent("n", "d", LocalDateTime.now(), null);

        assertEquals("redirect:/login", view);
        verify(eventService, never()).saveEvent(any());
    }

    @Test
    void addEvent_success_createsEventAndSaves() {
        when(currentUserService.hasHousehold()).thenReturn(true);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));

        LocalDateTime dt = LocalDateTime.now().plusDays(3);
        String view = eventController.addEvent("Birthday", "Cake", dt, null);

        assertEquals("redirect:/events", view);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).saveEvent(captor.capture());
        Event created = captor.getValue();
        assertEquals("Birthday", created.getName());
        assertEquals("Cake", created.getDescription());
        assertEquals(dt, created.getEventDate());
        assertEquals(sampleUser, created.getUserid());
        assertEquals(sampleHousehold, created.getHousehold());
    }

    @Test
    void deleteEvent_noCurrentUser_redirectsToLogin() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        String view = eventController.deleteEvent(5L);

        assertEquals("redirect:/login", view);
        verify(eventService, never()).deleteEvent(anyLong());
    }

    @Test
    void deleteEvent_eventPresent_differentHousehold_doesNotDelete() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        Household other = new Household();
        other.setId(99L);
        Event otherEvent = new Event();
        otherEvent.setId(7L);
        otherEvent.setHousehold(other);
        when(eventService.getEventById(7L)).thenReturn(Optional.of(otherEvent));

        String view = eventController.deleteEvent(7L);

        assertEquals("redirect:/events", view);
        verify(eventService, never()).deleteEvent(7L);
    }

    @Test
    void deleteEvent_eventPresent_sameHousehold_deletes() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        when(eventService.getEventById(5L)).thenReturn(Optional.of(sampleEvent));

        String view = eventController.deleteEvent(5L);

        assertEquals("redirect:/events", view);
        verify(eventService).deleteEvent(5L);
    }
}
