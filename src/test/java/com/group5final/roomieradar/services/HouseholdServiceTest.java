package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;

    @InjectMocks
    private HouseholdService householdService;

    private User creator;
    private Household existingHousehold;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");

        existingHousehold = new Household();
        existingHousehold.setId(10L);
        existingHousehold.setName("HouseA");
        existingHousehold.setPassword("storedPw");
    }

    @Test
    void createHousehold_success_savesHouseholdAndAssignsToCreator() {
        String name = "NewHouse";
        String rawPw = "secret";

        when(householdRepository.findByName(name)).thenReturn(Optional.empty());

        householdService.createHousehold(name, rawPw, creator);

        ArgumentCaptor<Household> captor = ArgumentCaptor.forClass(Household.class);
        verify(householdRepository).save(captor.capture());
        Household captured = captor.getValue();
        assertEquals(name, captured.getName());
        assertEquals(rawPw, captured.getPassword());

        // Service sets the same household object it created and saved
        assertSame(captured, creator.getHousehold());
    }

    @Test
    void createHousehold_nameExists_throwsIllegalArgumentException() {
        when(householdRepository.findByName(existingHousehold.getName())).thenReturn(Optional.of(existingHousehold));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> householdService.createHousehold(existingHousehold.getName(), "pw", creator));
        assertNotNull(ex.getMessage());
        verify(householdRepository, never()).save(any());
    }

    @Test
    void joinHousehold_success_withValidCredentials() {
        String name = "HouseA";
        String providedPw = "storedPw";

        when(householdRepository.findByNameAndPassword(name, providedPw)).thenReturn(Optional.of(existingHousehold));

        householdService.joinHousehold(name, providedPw, creator);

        assertEquals(existingHousehold, creator.getHousehold());
    }

    @Test
    void joinHousehold_invalidCredentials_throwsIllegalArgumentException() {
        String name = "HouseA";
        String providedPw = "wrong";

        when(householdRepository.findByNameAndPassword(name, providedPw)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> householdService.joinHousehold(name, providedPw, creator));
        assertNotNull(ex.getMessage());
    }
}
