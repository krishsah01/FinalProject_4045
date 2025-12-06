// src/test/java/com/group5final/roomieradar/controllers/AuthControllerTest.java
package com.group5final.roomieradar.controllers;

import com.group5final.roomieradar.dto.UserRegistrationDTO;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

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
    private UserService userService;

    @Mock
    private Model model;
    
    @Mock
    private BindingResult bindingResult;

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
        verify(model).addAttribute(eq("userRegistrationDTO"), any(UserRegistrationDTO.class));
    }

    @Test
    void registerUser_validationErrors_returnsSignup() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);
        when(bindingResult.hasErrors()).thenReturn(true);

        UserRegistrationDTO dto = new UserRegistrationDTO();
        String view = authController.registerUser(dto, bindingResult, model);

        assertEquals("signup", view);
        verify(model).addAttribute("households", households);
        verify(userService, never()).registerUser(any());
    }

    @Test
    void registerUser_serviceThrowsException_returnsSignupWithError() {
        List<Household> households = Collections.singletonList(sampleHousehold);
        when(householdRepository.findAll()).thenReturn(households);
        when(bindingResult.hasErrors()).thenReturn(false);
        
        doThrow(new IllegalArgumentException("Some error")).when(userService).registerUser(any());

        UserRegistrationDTO dto = new UserRegistrationDTO();
        String view = authController.registerUser(dto, bindingResult, model);

        assertEquals("signup", view);
        verify(model).addAttribute("error", "Some error");
        verify(model).addAttribute("households", households);
    }

    @Test
    void registerUser_success_redirectsToLogin() {
        when(bindingResult.hasErrors()).thenReturn(false);
        
        UserRegistrationDTO dto = new UserRegistrationDTO();
        String view = authController.registerUser(dto, bindingResult, model);

        assertEquals("redirect:/login?registered=true", view);
        verify(userService).registerUser(dto);
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
