// java
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Bill;
import com.group5final.roomieradar.entities.BillSplit;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.enums.SplitStatus;
import com.group5final.roomieradar.repositories.BillRepository;
import com.group5final.roomieradar.repositories.BillSplitRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillSplitRepository billSplitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private BillService billService;

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
    void createBill_splitEqually_success() {
        List<User> members = asList(
                createUser(2L, "alice"),
                createUser(3L, "bob")
        );

        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> {
            Bill b = i.getArgument(0);
            b.setId(10L);
            return b;
        });
        when(userRepository.findByHouseholdId(household.getId())).thenReturn(members);

        Bill result = billService.createBill("Rent", BigDecimal.valueOf(200), "monthly", LocalDateTime.now(), null, true);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(billRepository, times(1)).save(any(Bill.class));
        verify(billSplitRepository, times(members.size())).save(any(BillSplit.class));
    }

    @Test
    void createBill_noHousehold_throws() {
        User noHouse = new User();
        noHouse.setId(5L);
        noHouse.setHousehold(null);
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(noHouse));

        assertThrows(IllegalStateException.class, () ->
                billService.createBill("x", BigDecimal.ONE, null, null, List.of(2L), false)
        );

        verify(billRepository, never()).save(any());
    }

    @Test
    void createBill_noUsersToSplit_throws() {
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(currentUser));
        List<Long> userIds = List.of(999L);
        when(userRepository.findAllById(userIds)).thenReturn(List.of()); // empty -> triggers error
        when(billRepository.save(any(Bill.class))).thenAnswer(i -> {
            Bill b = i.getArgument(0);
            b.setId(11L);
            return b;
        });

        assertThrows(IllegalArgumentException.class, () ->
                billService.createBill("Utilities", BigDecimal.valueOf(90), null, null, userIds, false)
        );

        verify(billSplitRepository, never()).save(any());
    }

    @Test
    void getBillsByUser_returnsList() {
        Bill b1 = new Bill();
        b1.setId(21L);
        when(billRepository.findBillsByUserId(1L)).thenReturn(List.of(b1));

        List<Bill> bills = billService.getBillsByUser(1L);
        assertEquals(1, bills.size());
        assertEquals(21L, bills.get(0).getId());
        verify(billRepository).findBillsByUserId(1L);
    }

    @Test
    void getBillSplitsByBill_returnsList() {
        BillSplit s = new BillSplit();
        s.setId(31L);
        when(billSplitRepository.findByBillId(50L)).thenReturn(List.of(s));

        List<BillSplit> splits = billService.getBillSplitsByBill(50L);
        assertEquals(1, splits.size());
        assertEquals(31L, splits.get(0).getId());
        verify(billSplitRepository).findByBillId(50L);
    }

    @Test
    void settleSplit_success_updatesStatus() {
        BillSplit split = new BillSplit();
        split.setId(40L);
        User debtor = createUser(2L, "debtor");
        split.setUser(debtor);
        split.setStatus(SplitStatus.UNPAID);

        when(billSplitRepository.findById(40L)).thenReturn(Optional.of(split));

        billService.settleSplit(40L, 2L);

        ArgumentCaptor<BillSplit> captor = ArgumentCaptor.forClass(BillSplit.class);
        verify(billSplitRepository).save(captor.capture());
        assertEquals(SplitStatus.PENDING_APPROVAL, captor.getValue().getStatus());
    }

    @Test
    void settleSplit_wrongUser_throws() {
        BillSplit split = new BillSplit();
        split.setId(41L);
        split.setUser(createUser(99L, "someone"));
        split.setStatus(SplitStatus.UNPAID);
        when(billSplitRepository.findById(41L)).thenReturn(Optional.of(split));

        assertThrows(IllegalArgumentException.class, () -> billService.settleSplit(41L, 2L));
    }

    @Test
    void settleSplit_wrongStatus_throws() {
        BillSplit split = new BillSplit();
        split.setId(42L);
        split.setUser(createUser(2L, "debtor"));
        split.setStatus(SplitStatus.PENDING_APPROVAL);
        when(billSplitRepository.findById(42L)).thenReturn(Optional.of(split));

        assertThrows(IllegalStateException.class, () -> billService.settleSplit(42L, 2L));
    }

    @Test
    void settleSplit_notFound_throws() {
        when(billSplitRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> billService.settleSplit(999L, 2L));
    }

    @Test
    void approveSplit_success_updatesStatus() {
        Bill bill = new Bill();
        User creator = createUser(5L, "creator");
        bill.setCreatedBy(creator);

        BillSplit split = new BillSplit();
        split.setId(60L);
        split.setBill(bill);
        split.setStatus(SplitStatus.PENDING_APPROVAL);

        when(billSplitRepository.findById(60L)).thenReturn(Optional.of(split));

        billService.approveSplit(60L, 5L);

        ArgumentCaptor<BillSplit> captor = ArgumentCaptor.forClass(BillSplit.class);
        verify(billSplitRepository).save(captor.capture());
        assertEquals(SplitStatus.PAID, captor.getValue().getStatus());
    }

    @Test
    void approveSplit_wrongCreator_throws() {
        Bill bill = new Bill();
        bill.setCreatedBy(createUser(10L, "other"));
        BillSplit split = new BillSplit();
        split.setId(61L);
        split.setBill(bill);
        split.setStatus(SplitStatus.PENDING_APPROVAL);
        when(billSplitRepository.findById(61L)).thenReturn(Optional.of(split));

        assertThrows(IllegalArgumentException.class, () -> billService.approveSplit(61L, 5L));
    }

    @Test
    void approveSplit_wrongStatus_throws() {
        Bill bill = new Bill();
        bill.setCreatedBy(createUser(5L, "creator"));
        BillSplit split = new BillSplit();
        split.setId(62L);
        split.setBill(bill);
        split.setStatus(SplitStatus.UNPAID);
        when(billSplitRepository.findById(62L)).thenReturn(Optional.of(split));

        assertThrows(IllegalStateException.class, () -> billService.approveSplit(62L, 5L));
    }

    @Test
    void approveSplit_notFound_throws() {
        when(billSplitRepository.findById(9999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> billService.approveSplit(9999L, 1L));
    }

    @Test
    void deleteBill_success_callsRepository() {
        doNothing().when(billRepository).deleteById(70L);
        billService.deleteBill(70L);
        verify(billRepository).deleteById(70L);
    }

    @Test
    void deleteBill_repositoryThrows_propagates() {
        doThrow(new RuntimeException("boom")).when(billRepository).deleteById(71L);
        assertThrows(RuntimeException.class, () -> billService.deleteBill(71L));
    }

    // helper
    private User createUser(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setHousehold(household);
        return u;
    }
}