// java
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @BeforeEach
    void setup() {
        // ensure a clean security context before each test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // clear context to avoid leaking auth between tests
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_noAuthentication_returnsEmpty() {
        SecurityContextHolder.clearContext(); // no authentication present

        Optional<User> res = currentUserService.getCurrentUser();

        assertFalse(res.isPresent());
        verifyNoInteractions(userRepository);
    }

    @Test
    void getCurrentUser_userNotFound_returnsEmpty() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("unknown");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> res = currentUserService.getCurrentUser();

        assertFalse(res.isPresent());
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void getCurrentUser_userFound_returnsUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("tester");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setId(7L);
        user.setUsername("tester");
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));

        Optional<User> res = currentUserService.getCurrentUser();

        assertTrue(res.isPresent());
        assertSame(user, res.get());
        verify(userRepository).findByUsername("tester");
    }

    @Test
    void hasHousehold_noAuthentication_returnsFalse() {
        SecurityContextHolder.clearContext();

        boolean has = currentUserService.hasHousehold();

        assertFalse(has);
        verifyNoInteractions(userRepository);
    }

    @Test
    void hasHousehold_userWithoutHousehold_returnsFalse() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("noHouse");
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUsername("noHouse");
        user.setHousehold(null);
        when(userRepository.findByUsername("noHouse")).thenReturn(Optional.of(user));

        boolean has = currentUserService.hasHousehold();

        assertFalse(has);
        verify(userRepository).findByUsername("noHouse");
    }

    @Test
    void hasHousehold_userWithHousehold_returnsTrue() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("withHouse");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Household hh = new Household();
        hh.setId(1L);
        hh.setName("Home");

        User user = new User();
        user.setUsername("withHouse");
        user.setHousehold(hh);
        when(userRepository.findByUsername("withHouse")).thenReturn(Optional.of(user));

        boolean has = currentUserService.hasHousehold();

        assertTrue(has);
        verify(userRepository).findByUsername("withHouse");
    }
}
