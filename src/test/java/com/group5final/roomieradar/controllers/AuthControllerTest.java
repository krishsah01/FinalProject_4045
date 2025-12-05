// src/test/java/com/group5final/roomieradar/controllers/AuthControllerTest.java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private Household sampleHousehold;

    @BeforeEach
    void setUp() {
        sampleHousehold = new Household();
        sampleHousehold.setId(1L);
        sampleHousehold.setName("HouseA");
        sampleHousehold.setPassword("pass");
    }

    @Test
    void login_withErrorAndLogout_addsMessagesAndHouseholds() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.login("err", "out", model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Invalid username or password");
        verify(model).addAttribute("message", "You have been logged out successfully");
        verify(model).addAttribute("households", households);
    }

    @Test
    void signup_get_addsHouseholdsAndReturnsSignup() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.signup(model);

        assertEquals("signup", view);
        verify(model).addAttribute("households", households);
    }

    @Test
    void registerUser_usernameExists_returnsSignupWithError() {
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.registerUser("existing", "e@mail", "pw", "create",
                null, null, null, null, null, model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Username already exists");
        verify(model).addAttribute("households", households);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_join_missingParams_returnsSignupWithError() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        // Missing householdId / householdName / householdPassword
        String view = authController.registerUser("u", "a@b", "pw", "join",
                null, null, null, null, null, model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Please select a household and enter the password");
        verify(model).addAttribute("households", households);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_join_invalidHouseholdPassword_returnsSignupWithError() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(householdRepository.findByNameAndPassword("HouseA", "wrong")).thenReturn(Optional.empty());
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.registerUser("newuser", "u@x", "pw", "join",
                1L, "HouseA", "wrong", null, null, model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Invalid household password");
        verify(model).addAttribute("households", households);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_join_success_savesUserAndRedirects() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(householdRepository.findByNameAndPassword("HouseA", "pass")).thenReturn(Optional.of(sampleHousehold));

        String view = authController.registerUser("newuser", "u@x", "pw", "join",
                1L, "HouseA", "pass", null, null, model);

        assertEquals("redirect:/login?registered=true", view);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertEquals("u@x", saved.getEmail());
        assertEquals("pw", saved.getPassword());
        assertNotNull(saved.getHousehold());
        assertEquals(sampleHousehold, saved.getHousehold());
    }

    @Test
    void registerUser_create_missingFields_returnsSignupWithError() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.registerUser("u", "e@mail", "pw", "create",
                null, null, null, "", "", model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Please provide household name and password");
        verify(model).addAttribute("households", households);
        verify(householdRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_create_nameExists_returnsSignupWithError() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());
        when(householdRepository.findByName("HouseA")).thenReturn(Optional.of(sampleHousehold));
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);

        String view = authController.registerUser("u", "e@mail", "pw", "create",
                null, null, null, "HouseA", "pwhouse", model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Household name already exists");
        verify(model).addAttribute("households", households);
        verify(householdRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_create_success_createsHouseholdAndUser() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());
        when(householdRepository.findByName("NewHouse")).thenReturn(Optional.empty());
        Household savedHousehold = new Household();
        savedHousehold.setId(2L);
        savedHousehold.setName("NewHouse");
        savedHousehold.setPassword("hpw");
        when(householdRepository.save(any())).thenReturn(savedHousehold);

        String view = authController.registerUser("u", "e@mail", "pw", "create",
                null, null, null, "NewHouse", "hpw", model);

        assertEquals("redirect:/login?registered=true", view);

        ArgumentCaptor<Household> hhCaptor = ArgumentCaptor.forClass(Household.class);
        verify(householdRepository).save(hhCaptor.capture());
        Household created = hhCaptor.getValue();
        assertEquals("NewHouse", created.getName());
        assertEquals("hpw", created.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("u", savedUser.getUsername());
        assertEquals("e@mail", savedUser.getEmail());
        assertEquals("pw", savedUser.getPassword());
        assertNotNull(savedUser.getHousehold());
    }

    @Test
    void getHouseholdName_found_returnsName() {
        when(householdRepository.findById(1L)).thenReturn(Optional.of(sampleHousehold));

        String res = authController.getHouseholdName(1L);
        assertEquals("HouseA", res);
    }

    @Test
    void getHouseholdName_notFound_returnsEmpty() {
        when(householdRepository.findById(99L)).thenReturn(Optional.empty());

        String res = authController.getHouseholdName(99L);
        assertEquals("", res);
    }
}
