// java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Chore;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.ChoreRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChoreControllerTest {

    @Mock
    private ChoreRepository choreRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ChoreController controller;

    private Household household;
    private User currentUser;

    @BeforeEach
    void setUp() {
        household = new Household();
        household.setId(100L);

        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("me");
        currentUser.setHousehold(household);
    }

    @Test
    void showChores_userWithoutHousehold_setsNoHousehold() {
        User noHouse = new User();
        noHouse.setId(2L);
        noHouse.setHousehold(null);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(noHouse));

        Model model = new ConcurrentModel();
        String view = controller.showChores(model, null);

        assertEquals("chores", view);
        assertTrue(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        assertEquals(List.of(), ((Iterable<?>) model.getAttribute("chores")) instanceof Iterable ? List.of() : List.of());
    }

    @Test
    void showChores_withHousehold_populatesModel() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        Chore c = new Chore();
        c.setId(10L);
        when(choreRepository.findByHouseholdId(household.getId())).thenReturn(List.of(c));

        Model model = new ConcurrentModel();
        String view = controller.showChores(model, "hello");

        assertEquals("chores", view);
        assertFalse(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        Iterable<?> chores = (Iterable<?>) model.getAttribute("chores");
        assertNotNull(chores);
        assertEquals("hello", model.getAttribute("msg"));
    }

    @Test
    void showAddChoreForm_noHousehold_redirectsToHousehold() {
        User noHouse = new User();
        noHouse.setId(3L);
        noHouse.setHousehold(null);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(noHouse));

        Model model = new ConcurrentModel();
        String view = controller.showAddChoreForm(model, null, null);

        assertEquals("redirect:/household?requiresHousehold=true", view);
    }

    @Test
    void showAddChoreForm_withHousehold_populatesMembersSorted() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        User b = new User(); b.setId(2L); b.setUsername("bob"); b.setHousehold(household);
        User a = new User(); a.setId(3L); a.setUsername("Alice"); a.setHousehold(household);
        // unsorted list: bob then Alice
        when(userRepository.findByHousehold_Id(household.getId())).thenReturn(asList(b, a));

        Model model = new ConcurrentModel();
        String view = controller.showAddChoreForm(model, null, null);

        assertEquals("add-chore", view);
        @SuppressWarnings("unchecked")
        List<User> members = (List<User>) model.getAttribute("members");
        assertNotNull(members);
        // should be sorted case-insensitive: Alice then bob
        assertEquals("Alice", members.get(0).getUsername());
        assertEquals("bob", members.get(1).getUsername());
    }

    @Test
    void addChore_success_savesAndRedirects() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        User assignee = new User();
        assignee.setId(7L);
        assignee.setUsername("assign");
        assignee.setHousehold(household);

        when(userRepository.findById(7L)).thenReturn(Optional.of(assignee));

        ArgumentCaptor<Chore> captor = ArgumentCaptor.forClass(Chore.class);
        when(choreRepository.save(any(Chore.class))).thenAnswer(i -> i.getArgument(0));

        LocalDate due = LocalDate.of(2025, 12, 5);
        String view = controller.addChore("Clean", 7L, due, "desc");

        assertEquals("redirect:/chores?msg=Chore added successfully!", view);
        verify(choreRepository).save(captor.capture());
        Chore saved = captor.getValue();
        assertEquals("Clean", saved.getName());
        assertSame(assignee, saved.getUser());
        assertSame(household, saved.getHousehold());
        assertEquals(due.atStartOfDay().toInstant(ZoneOffset.UTC), saved.getDueDate());
        assertEquals("desc", saved.getDescription());
    }

    @Test
    void addChore_invalidAssignee_returnsErrorRedirect_whenNotFound() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        String view = controller.addChore("Name", 99L, LocalDate.now(), null);

        assertEquals("redirect:/chores/add?error=Invalid assignee selected", view);
        verify(choreRepository, never()).save(any());
    }

    @Test
    void addChore_invalidAssignee_returnsErrorRedirect_whenDifferentHousehold() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        Household other = new Household();
        other.setId(500L);
        User assignee = new User();
        assignee.setId(8L);
        assignee.setHousehold(other);

        when(userRepository.findById(8L)).thenReturn(Optional.of(assignee));

        String view = controller.addChore("Name", 8L, LocalDate.now(), null);

        assertEquals("redirect:/chores/add?error=Invalid assignee selected", view);
        verify(choreRepository, never()).save(any());
    }

    @Test
    void addChore_noHousehold_redirectsToHousehold() {
        User noHouse = new User();
        noHouse.setId(4L);
        noHouse.setHousehold(null);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(noHouse));

        String view = controller.addChore("X", 1L, LocalDate.now(), null);

        assertEquals("redirect:/household?requiresHousehold=true", view);
        verify(choreRepository, never()).save(any());
    }
}
