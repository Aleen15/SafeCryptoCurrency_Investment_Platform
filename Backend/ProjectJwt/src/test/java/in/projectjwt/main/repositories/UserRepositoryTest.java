package in.projectjwt.main.repositories;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import in.projectjwt.main.entities.User;

@SpringBootTest
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes the mocks

        // Sample User Object
        user = new User();
        user.setId(1);
        user.setFullName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("Password@123");
        user.setAddress("123 Main St");
        user.setPhone("1234567890");
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("johndoe@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userRepository.findByEmail("johndoe@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("johndoe@example.com", result.get().getEmail());
    }

    @Test
    public void testExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("johndoe@example.com")).thenReturn(true);

        // Act
        boolean result = userRepository.existsByEmail("johndoe@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testFindById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userRepository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    public void testFindByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void testExistsByEmail_UserDoesNotExist() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result);
    }
}
