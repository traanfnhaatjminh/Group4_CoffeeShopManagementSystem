package pl.codeleak.demos.sbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;


public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByUsername() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Users result = userService.getUserByUsername("johndoe");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    void testSaveUser() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByPhone(anyString())).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.saveUser(user);

        verify(userRepository, times(1)).insertUser(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt()
        );
    }

    @Test
    void testFindByUsername() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        Users result = userService.findByUsername("johndoe");

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    void testSave() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users result = userService.save(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPass());
    }

    @Test
    void testCheckPassword() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "encodedPassword", 1, 1);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean result = userService.checkPassword(user, "password");

        assertTrue(result);
    }

    @Test
    void testUpdatePassword() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "oldPassword", 1, 1);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        userService.updatePassword(user, "newPassword");

        verify(userRepository, times(1)).save(argThat(u -> "newEncodedPassword".equals(u.getPass())));
    }

    @Test
    void testIsEmailTaken() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        boolean result = userService.isEmailTaken("john@example.com");

        assertTrue(result);
    }

    @Test
    void testIsPhoneTaken() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByPhone(anyString())).thenReturn(user);

        boolean result = userService.isPhoneTaken("123456789");

        assertTrue(result);
    }

    @Test
    void testIsUsernameTaken() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        boolean result = userService.isUsernameTaken("johndoe");

        assertTrue(result);
    }

    @Test
    void testFindAll() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<Users> result = userService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteById() {
        userService.deleteById(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testFindById() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        Users result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getUid());
    }


    @Test
    void testFindPaginated() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        Page<Users> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Users> result = userService.findPaginated(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindByRole() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        Page<Users> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findByRole(anyInt(), any(Pageable.class))).thenReturn(page);

        Page<Users> result = userService.findByRole(1, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearch() {
        Users user = new Users(1, "John Doe", "2000-01-01", "john@example.com", "123456789", "123 Street", "avatar.png", "johndoe", "password", 1, 1);
        Page<Users> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.searchByKeywordAndRole(anyString(), anyInt(), any(Pageable.class))).thenReturn(page);

        Page<Users> result = userService.search("keyword", 1, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
