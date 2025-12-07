// java
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdServiceTest {

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private HouseholdService householdService;

    private User creator;
    private Household existingHousehold;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(10L);
        creator.setUsername("creator");

        existingHousehold = new Household();
        existingHousehold.setId(1L);
        existingHousehold.setName("HouseA");
        existingHousehold.setPassword("storedPw");
    }

    @Test
    void createHousehold_success_savesHouseholdAndAssignsToCreator() {
        String name = "NewHouse";
        String rawPw = "secret";
        String encoded = "encodedSecret";

        when(householdRepository.findByName(name)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPw)).thenReturn(encoded);

        Household saved = new Household();
        saved.setId(2L);
        saved.setName(name);
        saved.setPassword(encoded);
        when(householdRepository.save(any(Household.class))).thenReturn(saved);

        Household result = householdService.createHousehold(name, rawPw, creator);

        assertSame(saved, result);
        assertEquals(name, result.getName());
        assertEquals(encoded, result.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertSame(saved, userCaptor.getValue().getHousehold());
    }

    @Test
    void createHousehold_nameExists_throwsIllegalArgumentException() {
        when(householdRepository.findByName(existingHousehold.getName())).thenReturn(Optional.of(existingHousehold));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> householdService.createHousehold(existingHousehold.getName(), "pw", creator));
        assertTrue(ex.getMessage().contains("Household name already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void joinHousehold_success_withEncodedPassword_matchesAndSavesUser() {
        String name = "HouseA";
        String providedPw = "secret";
        existingHousehold.setPassword("encodedValue");

        when(householdRepository.findByName(name)).thenReturn(Optional.of(existingHousehold));
        when(passwordEncoder.matches(providedPw, existingHousehold.getPassword())).thenReturn(true);

        householdService.joinHousehold(name, providedPw, creator);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertSame(existingHousehold, userCaptor.getValue().getHousehold());
    }

    @Test
    void joinHousehold_success_plaintextFallback_whenMatchesRawStored() {
        String name = "HouseA";
        String providedPw = "plaintextPw";
        // Simulate stored password being plain text (migration)
        existingHousehold.setPassword(providedPw);

        when(householdRepository.findByName(name)).thenReturn(Optional.of(existingHousehold));
        when(passwordEncoder.matches(providedPw, existingHousehold.getPassword())).thenReturn(false);

        householdService.joinHousehold(name, providedPw, creator);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertSame(existingHousehold, userCaptor.getValue().getHousehold());
    }

    @Test
    void joinHousehold_householdNotFound_throwsIllegalArgumentException() {
        when(householdRepository.findByName("Missing")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> householdService.joinHousehold("Missing", "pw", creator));
        assertTrue(ex.getMessage().contains("Household not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void joinHousehold_invalidPassword_throwsIllegalArgumentException() {
        String name = "HouseA";
        String providedPw = "wrong";
        existingHousehold.setPassword("encodedVal");

        when(householdRepository.findByName(name)).thenReturn(Optional.of(existingHousehold));
        when(passwordEncoder.matches(providedPw, existingHousehold.getPassword())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> householdService.joinHousehold(name, providedPw, creator));
        assertTrue(ex.getMessage().contains("Invalid household password"));
        verify(userRepository, never()).save(any());
    }
}