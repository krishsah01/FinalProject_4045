// java
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.dto.UserRegistrationDTO;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDTO();
        dto.setUsername("newuser");
        dto.setEmail("user@example.com");
        dto.setPassword("plainPass");
    }

    @Test
    void registerUser_success_noHousehold_savesUser() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedUserPass");

        User saved = new User();
        saved.setId(5L);
        saved.setUsername("newuser");
        saved.setPassword("encodedUserPass");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.registerUser(dto);

        assertSame(saved, result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertEquals("newuser", toSave.getUsername());
        assertEquals("encodedUserPass", toSave.getPassword());
        assertNull(toSave.getHousehold());
    }

    @Test
    void registerUser_usernameExists_throwsIllegalArgumentException() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("newuser");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_joinHousehold_success_withEncoderMatch() {
        dto.setHouseholdAction("join");
        dto.setHouseholdName("HouseA");
        dto.setHouseholdPassword("providedPw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        Household hh = new Household();
        hh.setId(10L);
        hh.setName("HouseA");
        hh.setPassword("encodedStored");
        when(householdRepository.findByName("HouseA")).thenReturn(Optional.of(hh));

        when(passwordEncoder.matches("providedPw", "encodedStored")).thenReturn(true);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedUserPass");

        User saved = new User();
        saved.setId(6L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.registerUser(dto);

        assertSame(saved, result);
        ArgumentCaptor<User> uc = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(uc.capture());
        assertSame(hh, uc.getValue().getHousehold());
    }

    @Test
    void registerUser_joinHousehold_success_plaintextFallback() {
        dto.setHouseholdAction("join");
        dto.setHouseholdName("HouseA");
        dto.setHouseholdPassword("plainStoredPw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        Household hh = new Household();
        hh.setId(11L);
        // stored as plain text (migration scenario)
        hh.setPassword("plainStoredPw");
        when(householdRepository.findByName("HouseA")).thenReturn(Optional.of(hh));

        // encoder returns false but plaintext equals stored
        when(passwordEncoder.matches("plainStoredPw", "plainStoredPw")).thenReturn(false);
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedUserPass");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        User result = userService.registerUser(dto);

        assertNotNull(result);
        ArgumentCaptor<User> uc = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(uc.capture());
        assertSame(hh, uc.getValue().getHousehold());
    }

    @Test
    void registerUser_joinHousehold_householdNotFound_throwsIllegalArgumentException() {
        dto.setHouseholdAction("join");
        dto.setHouseholdName("MissingHouse");
        dto.setHouseholdPassword("pw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(householdRepository.findByName("MissingHouse")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("Household not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_joinHousehold_invalidPassword_throwsIllegalArgumentException() {
        dto.setHouseholdAction("join");
        dto.setHouseholdName("HouseA");
        dto.setHouseholdPassword("wrongPw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        Household hh = new Household();
        hh.setId(12L);
        hh.setPassword("encodedStored");
        when(householdRepository.findByName("HouseA")).thenReturn(Optional.of(hh));
        when(passwordEncoder.matches("wrongPw", "encodedStored")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("Invalid household password"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_createHousehold_success_createsHouseholdAndAssigns() {
        dto.setHouseholdAction("create");
        dto.setNewHouseholdName("NewHouse");
        dto.setNewHouseholdPassword("housePw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(householdRepository.findByName("NewHouse")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("housePw")).thenReturn("encodedHousePw");
        Household createdHouse = new Household();
        createdHouse.setId(99L);
        createdHouse.setName("NewHouse");
        createdHouse.setPassword("encodedHousePw");
        when(householdRepository.save(any(Household.class))).thenReturn(createdHouse);

        when(passwordEncoder.encode("plainPass")).thenReturn("encodedUserPass");
        User savedUser = new User();
        savedUser.setId(77L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertSame(savedUser, result);
        ArgumentCaptor<Household> hc = ArgumentCaptor.forClass(Household.class);
        verify(householdRepository).save(hc.capture());
        assertEquals("NewHouse", hc.getValue().getName());

        ArgumentCaptor<User> uc = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(uc.capture());
        assertSame(createdHouse, uc.getValue().getHousehold());
    }

    @Test
    void registerUser_createHousehold_nameExists_throwsIllegalArgumentException() {
        dto.setHouseholdAction("create");
        dto.setNewHouseholdName("Existing");
        dto.setNewHouseholdPassword("pw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        Household exists = new Household();
        exists.setId(2L);
        exists.setName("Existing");
        when(householdRepository.findByName("Existing")).thenReturn(Optional.of(exists));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("Household name already exists"));
        verify(householdRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}