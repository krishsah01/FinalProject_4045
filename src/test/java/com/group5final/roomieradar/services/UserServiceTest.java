package com.group5final.roomieradar.services;

import com.group5final.roomieradar.dto.UserRegistrationDTO;
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

        userService.registerUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertEquals("newuser", toSave.getUsername());
        assertEquals("encodedUserPass", toSave.getPassword());
        assertNull(toSave.getHousehold());
    }

    @Test
    void registerUser_usernameExists_throwsException() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("newuser");
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(existing));

        assertThrows(Exception.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }
}
