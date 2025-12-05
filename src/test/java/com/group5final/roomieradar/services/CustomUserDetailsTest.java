// java
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_userFound_returnsUserDetailsWithRoleUser() {
        User u = new User();
        u.setUsername("tester");
        u.setPassword("secret");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(u));

        UserDetails details = customUserDetailsService.loadUserByUsername("tester");

        assertNotNull(details);
        assertEquals("tester", details.getUsername());
        assertEquals("secret", details.getPassword());

        boolean hasRoleUser = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_USER"::equals);
        assertTrue(hasRoleUser, "Expected authority ROLE_USER to be present");

        verify(userRepository).findByUsername("tester");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing"));

        assertTrue(ex.getMessage().contains("missing"));
        verify(userRepository).findByUsername("missing");
    }
}
