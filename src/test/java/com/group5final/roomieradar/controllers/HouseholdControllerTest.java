// java
        package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.services.HouseholdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HouseholdService householdService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Model model;

    @InjectMocks
    private HouseholdController householdController;

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
    }

    @Test
    void household_noUser_addsMessageAndWarningAndReturnsView() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        String view = householdController.household("req", "hello", model);

        assertEquals("household", view);
        verify(model).addAttribute("me", null);
        verify(model).addAttribute("message", "hello");
        verify(model).addAttribute("warning", "Join or create a household to access chores, bills, and events.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void household_userWithHousehold_addsMembersSortedAndCount() {
        User u1 = new User(); u1.setUsername("beta");
        User u2 = new User(); u2.setUsername("Alpha");
        List<User> members = Arrays.asList(u1, u2); // unsorted
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        when(userRepository.findByHousehold_Id(sampleHousehold.getId())).thenReturn(members);

        String view = householdController.household(null, null, model);

        assertEquals("household", view);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("members"), captor.capture());
        List<User> added = captor.getValue();
        assertEquals(2, added.size());
        // should be sorted ignoring case: "Alpha" then "beta"
        assertEquals("Alpha", added.get(0).getUsername());
        assertEquals("beta", added.get(1).getUsername());

        verify(model).addAttribute("membersCount", 2);
        verify(model).addAttribute("me", sampleUser);
    }

    @Test
    void joinHousehold_notLoggedIn_redirectsToLogin() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        String view = householdController.joinHousehold("HouseA", "pw", model);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(householdService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void joinHousehold_invalidCredentials_returnsHouseholdWithError() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        doThrow(new IllegalArgumentException("Invalid household name or password"))
                .when(householdService).joinHousehold("HouseA", "wrong", sampleUser);

        String view = householdController.joinHousehold("HouseA", "wrong", model);

        assertEquals("household", view);
        verify(model).addAttribute("error", "Invalid household name or password");
        // controller may add "me" more than once (joinHousehold -> household()), allow at least one invocation
        verify(model, atLeastOnce()).addAttribute("me", sampleUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void joinHousehold_success_setsHouseholdAndSavesAndRedirects() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        doNothing().when(householdService).joinHousehold("HouseA", "hpw", sampleUser);

        String view = householdController.joinHousehold("HouseA", "hpw", model);

        assertEquals("redirect:/household?msg=Joined", view);
        verify(householdService).joinHousehold("HouseA", "hpw", sampleUser);
    }

    @Test
    void createHousehold_notLoggedIn_redirectsToLogin() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());

        String view = householdController.createHousehold("New", "pw", model);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(householdService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void createHousehold_missingFields_returnsHouseholdWithError() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));

        String view = householdController.createHousehold("", " ", model);

        assertEquals("household", view);
        verify(model).addAttribute("error", "Household name and password are required");
        // controller may call household(...) which also adds "me"
        verify(model, atLeastOnce()).addAttribute("me", sampleUser);
        verifyNoInteractions(householdService);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createHousehold_nameExists_returnsHouseholdWithError() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        doThrow(new IllegalArgumentException("Household name already exists"))
                .when(householdService).createHousehold("HouseA", "hpw", sampleUser);

        String view = householdController.createHousehold("HouseA", "hpw", model);

        assertEquals("household", view);
        verify(model).addAttribute("error", "Household name already exists");
        // controller may call household(...) which also adds "me"
        verify(model, atLeastOnce()).addAttribute("me", sampleUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createHousehold_success_createsHouseholdAndAssignsAndRedirects() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(sampleUser));
        Household saved = new Household();
        saved.setId(2L);
        saved.setName("NewHouse");
        saved.setPassword("npw");
        
        when(householdService.createHousehold("NewHouse", "npw", sampleUser)).thenReturn(saved);

        String view = householdController.createHousehold("NewHouse", "npw", model);

        assertEquals("redirect:/household?msg=Created", view);

        verify(householdService).createHousehold("NewHouse", "npw", sampleUser);
    }
}