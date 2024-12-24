package in.projectjwt.main.services;


import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.InvalidCredentialsException;
import in.projectjwt.main.exceptions.UserNotFoundException;
import in.projectjwt.main.repositories.UserRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;
import java.util.Optional;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private LoginUserDto loginUserDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setEmail("john@example.com");
        user.setPassword("password");

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("john@example.com");
        loginUserDto.setPassword("password");
    }

    @Test
    public void testRegister() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        ResponseEntity<Map<String, String>> response = authenticationService.register(user);

        // Assert
        assertNotNull(response);
        assertEquals("User registered successfully.", response.getBody().get("message"));
    }

    @Test
    public void testAuthenticate() {
        // Arrange
        when(userRepository.findByEmail(loginUserDto.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(null);

        // Act
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals("john@example.com", authenticatedUser.getEmail());
    }

    @Test
    public void testUpdateUserProfile() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User updatedUser = authenticationService.updateUserProfile(1, "John Doe Updated", "New Address", "123456789");

        // Assert
        assertNotNull(updatedUser);
        assertEquals("John Doe Updated", updatedUser.getFullName());
    }
}
