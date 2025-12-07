// java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Bill;
import com.group5final.roomieradar.entities.BillSplit;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.services.BillService;
import com.group5final.roomieradar.services.CurrentUserService;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillService billService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BillController controller;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
    }

    @Test
    void bills_noHousehold_returnsNoHouseholdModel() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        Model model = new ConcurrentModel();
        String view = controller.bills(model);

        assertEquals("bills", view);
        assertTrue(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        assertEquals(List.of(), model.getAttribute("bills"));
        assertEquals(List.of(), model.getAttribute("householdUsers"));
    }

    @Test
    void bills_withHousehold_populatesModel() {
        Household household = new Household();
        household.setId(5L);
        currentUser.setHousehold(household);

        Bill bill = new Bill();
        bill.setId(10L);

        User member = new User();
        member.setId(2L);
        member.setUsername("alice");

        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(billService.getBillsByUser(currentUser.getId())).thenReturn(List.of(bill));
        when(userRepository.findByHouseholdId(household.getId())).thenReturn(List.of(member));

        Model model = new ConcurrentModel();
        String view = controller.bills(model);

        assertEquals("bills", view);
        assertFalse(Boolean.TRUE.equals(model.getAttribute("noHousehold")));
        assertEquals(1, ((List<?>) model.getAttribute("bills")).size());
        assertEquals(1, ((List<?>) model.getAttribute("householdUsers")).size());
        assertEquals(currentUser.getId(), model.getAttribute("currentUserId"));
    }

    @Test
    void addBill_success_returnsSuccess() throws Exception {
        when(billService.createBill(
                anyString(),
                any(BigDecimal.class),
                any(),
                any(),
                anyList(),
                anyBoolean()))
                .thenReturn(new Bill());

        String result = controller.addBill(
                "Rent",
                BigDecimal.valueOf(1200),
                "Monthly rent",
                "2025-12-01T00:00:00",
                List.of(2L, 3L),
                false);

        assertEquals("success", result);
        verify(billService).createBill(eq("Rent"), eq(BigDecimal.valueOf(1200)), eq("Monthly rent"),
                any(LocalDateTime.class), eq(List.of(2L, 3L)), eq(false));
    }

    @Test
    void addBill_malformedDate_returnsError() {
        String result = controller.addBill(
                "Utilities",
                BigDecimal.valueOf(200),
                null,
                "not-a-date",
                null,
                true);

        assertTrue(result.startsWith("error:"));
        verify(billService, never()).createBill(
                anyString(), any(BigDecimal.class), any(), any(), anyList(), anyBoolean());
    }

    @Test
    void getBillSplits_success_returnsList() {
        BillSplit split = new BillSplit();
        split.setId(7L);
        when(billService.getBillSplitsByBill(10L)).thenReturn(List.of(split));

        List<BillSplit> result = controller.getBillSplits(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(7L, result.get(0).getId());
    }

    @Test
    void getBillSplits_serviceThrows_propagatesException() {
        when(billService.getBillSplitsByBill(99L)).thenThrow(new IllegalArgumentException("not found"));

        assertThrows(IllegalArgumentException.class, () -> controller.getBillSplits(99L));
    }

    @Test
    void settleSplit_success_returnsSuccess() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        // default doNothing for void; just call
        String result = controller.settleSplit(15L);
        assertEquals("success", result);
        verify(billService).settleSplit(15L, currentUser.getId());
    }

    @Test
    void settleSplit_unauthenticated_returnsError() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());
        String result = controller.settleSplit(20L);
        assertTrue(result.startsWith("error:"));
        verify(billService, never()).settleSplit(anyLong(), anyLong());
    }

    @Test
    void approveSplit_success_returnsSuccess() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        String result = controller.approveSplit(30L);
        assertEquals("success", result);
        verify(billService).approveSplit(30L, currentUser.getId());
    }

    @Test
    void approveSplit_failure_returnsErrorWhenServiceThrows() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        doThrow(new IllegalArgumentException("bad")).when(billService).approveSplit(40L, currentUser.getId());

        String result = controller.approveSplit(40L);
        assertTrue(result.startsWith("error:"));
    }

    @Test
    void deleteBill_success_returnsSuccess() {
        String result = controller.deleteBill(50L);
        assertEquals("success", result);
        verify(billService).deleteBill(50L);
    }

    @Test
    void deleteBill_failure_returnsError() {
        doThrow(new RuntimeException("boom")).when(billService).deleteBill(60L);
        String result = controller.deleteBill(60L);
        assertTrue(result.startsWith("error:"));
    }
}