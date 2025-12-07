// java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.CalendarItem;
import com.group5final.roomieradar.entities.Event;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.CalendarItemRepository;
import com.group5final.roomieradar.repositories.EventRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarControllerTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CalendarItemRepository calendarItemRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CalendarController controller;

    private User currentUser;
    private Household household;

    @BeforeEach
    void setUp() {
        household = new Household();
        household.setId(100L);

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setHousehold(household);
    }

    @Test
    void showCalendar_noHousehold_returnsNoHouseholdModel_whenUserHasNoHousehold() {
        User noHouse = new User();
        noHouse.setId(2L);
        noHouse.setHousehold(null);

        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(noHouse));

        Model model = new ConcurrentModel();
        String view = controller.showCalendar(null, model);

        assertEquals("calendar", view);
        assertTrue(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        assertEquals(List.of(), model.getAttribute("events"));
        assertEquals(List.of(), model.getAttribute("calendarItems"));
        assertEquals(List.of(), ((java.util.Map<?,?>)model.getAttribute("calendarItemInstances")).values().stream().toList());
    }

    @Test
    void showCalendar_withHousehold_populatesModel_and_instances() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        Event e = new Event();
        e.setId(5L);
        when(eventRepository.findByHouseholdOrderByEventDateAsc(household)).thenReturn(List.of(e));

        // CalendarItem spans two days
        CalendarItem ci = new CalendarItem();
        ci.setId(10L);
        ci.setName("Multi");
        LocalDateTime start = LocalDate.of(2025, 12, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(2025, 12, 2).atStartOfDay();
        ci.setDateStart(start);
        ci.setDateEnd(end);
        when(calendarItemRepository.findByHousehold(household)).thenReturn(List.of(ci));

        Model model = new ConcurrentModel();
        LocalDate focus = LocalDate.of(2025, 12, 01);
        String view = controller.showCalendar(focus, model);

        assertEquals("calendar", view);
        assertFalse(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        assertEquals(1, ((List<?>) model.getAttribute("events")).size());
        assertEquals(1, ((List<?>) model.getAttribute("calendarItems")).size());
        Object instancesObj = model.getAttribute("calendarItemInstances");
        assertNotNull(instancesObj);
        @SuppressWarnings("unchecked")
        java.util.Map<String, java.util.List<?>> instances = (java.util.Map<String, java.util.List<?>>) instancesObj;
        // two dates: 2025-12-01 and 2025-12-02
        assertTrue(instances.containsKey("2025-12-01"));
        assertTrue(instances.containsKey("2025-12-02"));
        assertEquals("2025-12-01", model.getAttribute("focusedDate"));
    }

    @Test
    void newCalendarItem_withDate_setsDateStart() {
        Model model = new ConcurrentModel();
        LocalDate date = LocalDate.of(2025, 11, 5);
        String view = controller.newCalendarItem(date, model);

        assertEquals("edit-calendar", view);
        Object calObj = model.getAttribute("calendar");
        assertNotNull(calObj);
        CalendarItem calendar = (CalendarItem) calObj;
        assertNotNull(calendar.getDateStart());
        assertEquals(date.atStartOfDay(), calendar.getDateStart());
    }

    @Test
    void newCalendarItem_withoutDate_returnsBlankCalendar() {
        Model model = new ConcurrentModel();
        String view = controller.newCalendarItem(null, model);

        assertEquals("edit-calendar", view);
        assertNotNull(model.getAttribute("calendar"));
    }

    @Test
    void editCalendarItem_found_returnsEditCalendar() {
        CalendarItem ci = new CalendarItem();
        ci.setId(20L);
        when(calendarItemRepository.findById(20L)).thenReturn(Optional.of(ci));

        Model model = new ConcurrentModel();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String view = controller.editCalendarItem(20L, model, ra);

        assertEquals("edit-calendar", view);
        assertSame(ci, model.getAttribute("calendar"));
    }

    @Test
    void editCalendarItem_notFound_redirectsWithError() {
        when(calendarItemRepository.findById(99L)).thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String view = controller.editCalendarItem(99L, model, ra);

        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item not found", ra.getFlashAttributes().get("error"));
    }

    @Test
    void createCalendarItem_brHasErrors_returnsEditWithoutSaving() {
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(true);

        CalendarItem form = new CalendarItem();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.createCalendarItem(form, br, ra);

        assertEquals("edit-calendar", view);
        verify(calendarItemRepository, never()).save(any());
    }

    @Test
    void createCalendarItem_noHousehold_redirectsWithError() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        CalendarItem form = new CalendarItem();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.createCalendarItem(form, br, ra);

        assertEquals("redirect:/calendar", view);
        assertEquals("No household available to attach item to", ra.getFlashAttributes().get("error"));
        verify(calendarItemRepository, never()).save(any());
    }

    @Test
    void createCalendarItem_success_savesAndRedirects() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        CalendarItem form = new CalendarItem();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.createCalendarItem(form, br, ra);

        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item created", ra.getFlashAttributes().get("message"));
        verify(calendarItemRepository).save(form);
        assertSame(household, form.getHousehold());
        assertSame(currentUser, form.getCreator());
    }

    @Test
    void updateCalendarItem_brHasErrors_returnsEdit() {
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(true);

        CalendarItem form = new CalendarItem();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.updateCalendarItem(1L, form, br, ra);
        assertEquals("edit-calendar", view);
        verify(calendarItemRepository, never()).save(any());
    }

    @Test
    void updateCalendarItem_notFound_redirectsWithError() {
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);
        when(calendarItemRepository.findById(2L)).thenReturn(Optional.empty());

        CalendarItem form = new CalendarItem();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.updateCalendarItem(2L, form, br, ra);
        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item not found", ra.getFlashAttributes().get("error"));
        verify(calendarItemRepository, never()).save(any());
    }

    @Test
    void updateCalendarItem_success_updatesAndSaves() {
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        CalendarItem existing = new CalendarItem();
        existing.setId(3L);
        existing.setName("old");
        existing.setDescription("d");
        existing.setDateStart(LocalDate.of(2025,1,1).atStartOfDay());
        existing.setDateEnd(LocalDate.of(2025,1,1).atStartOfDay());
        when(calendarItemRepository.findById(3L)).thenReturn(Optional.of(existing));

        CalendarItem form = new CalendarItem();
        form.setName("new");
        form.setDescription("newdesc");
        form.setDateStart(LocalDate.of(2025,2,1).atStartOfDay());
        form.setDateEnd(LocalDate.of(2025,2,2).atStartOfDay());

        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String view = controller.updateCalendarItem(3L, form, br, ra);

        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item updated", ra.getFlashAttributes().get("message"));
        verify(calendarItemRepository).save(existing);
        assertEquals("new", existing.getName());
        assertEquals("newdesc", existing.getDescription());
    }

    @Test
    void deleteCalendarItem_notFound_redirectsWithError() {
        when(calendarItemRepository.existsById(77L)).thenReturn(false);
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.deleteCalendarItem(77L, ra);
        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item not found", ra.getFlashAttributes().get("error"));
        verify(calendarItemRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteCalendarItem_success_deletesAndRedirects() {
        when(calendarItemRepository.existsById(88L)).thenReturn(true);
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.deleteCalendarItem(88L, ra);
        assertEquals("redirect:/calendar", view);
        assertEquals("Calendar item deleted", ra.getFlashAttributes().get("message"));
        verify(calendarItemRepository).deleteById(88L);
    }
}
