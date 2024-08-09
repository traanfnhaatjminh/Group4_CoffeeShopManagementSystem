package pl.codeleak.demos.sbt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;
import pl.codeleak.demos.sbt.service.CustomUserDetailsService;

public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFoundAndActive() {
        String username = "testuser";
        Users user = new Users(1, "Full Name", "2000-01-01", "email@example.com", "1234567890", "Address", "avatar.png", username, "password", 1, 1);
        when(userRepository.findByUsername(username)).thenReturn(user);

        var userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "unknownuser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void testLoadUserByUsername_UserNotActive() {
        String username = "inactiveuser";
        Users user = new Users(1, "Full Name", "2000-01-01", "email@example.com", "1234567890", "Address", "avatar.png", username, "password", 1, 0);
        when(userRepository.findByUsername(username)).thenReturn(user);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void testLoadUserByUsername_UserWithRoleUser() {
        String username = "regularuser";
        Users user = new Users(1, "Full Name", "2000-01-01", "email@example.com", "1234567890", "Address", "avatar.png", username, "password", 0, 1);
        when(userRepository.findByUsername(username)).thenReturn(user);

        var userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }
}

