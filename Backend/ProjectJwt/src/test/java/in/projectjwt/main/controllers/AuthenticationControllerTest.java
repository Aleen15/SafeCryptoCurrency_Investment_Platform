package in.projectjwt.main.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.dtos.OtpVerificationRequestdto;
import in.projectjwt.main.dtos.ResetPasswordRequestdto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.repositories.UserRepository;
import in.projectjwt.main.services.AuthenticationService;
import in.projectjwt.main.services.JwtService;
import in.projectjwt.main.services.PasswordResetService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    // Test register method
    @Test
    void testRegisterUser() throws Exception {
        // Arrange
        User user = new User();
        user.setFullName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        Map<String, String> mockResponse = new HashMap<>();
        mockResponse.put("message", "User registered successfully.");

        when(authenticationService.register(any(User.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(mockResponse));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content("{\"fullName\": \"John Doe\", \"email\": \"john.doe@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.message").value("User registered successfully."));
    }

    // Test login method
    @Test
    void testLogin() throws Exception {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("john.doe@example.com");
        loginUserDto.setPassword("password123");

        User authenticatedUser = new User();
        authenticatedUser.setId(1);
        authenticatedUser.setFullName("John Doe");
        authenticatedUser.setEmail("john.doe@example.com");

        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenReturn(authenticatedUser);
        when(jwtService.generateToken(any(User.class)))
                .thenReturn("mock-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"email\": \"john.doe@example.com\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    // Test getLoggedInUser method
    @Test
    void testGetLoggedInUser() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setFullName("John Doe");
        user.setEmail("john.doe@example.com");

        when(userRepo.findById(any(Long.class))).thenReturn(java.util.Optional.of(user));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        // Act & Assert
        mockMvc.perform(get("/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    // Test forgot password method
    @Test
    void testForgotPassword() throws Exception {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john.doe@example.com");

        doNothing().when(passwordResetService).sendResetLink(anyString());

        // Act & Assert
        mockMvc.perform(post("/auth/forgot-password")
                .contentType("application/json")
                .content("{\"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent successfully"));
    }

    // Test verify OTP method
    @Test
    void testVerifyOTP() throws Exception {
        // Arrange
        OtpVerificationRequestdto request = new OtpVerificationRequestdto();
        request.setEmail("john.doe@example.com");
        request.setOtp("123456");

        doNothing().when(passwordResetService).verifyOTP(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/auth/otp-verification")
                .contentType("application/json")
                .content("{\"email\": \"john.doe@example.com\", \"otp\": \"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP verified successfully"));
    }

    // Test reset password method
    @Test
    void testResetPassword() throws Exception {
        // Arrange
        ResetPasswordRequestdto request = new ResetPasswordRequestdto();
        request.setEmail("john.doe@example.com");
        request.setNewPassword("newpassword123");

        doNothing().when(passwordResetService).resetPassword(any(ResetPasswordRequestdto.class));

        // Act & Assert
        mockMvc.perform(post("/auth/reset-password")
                .contentType("application/json")
                .content("{\"email\": \"john.doe@example.com\", \"newPassword\": \"newpassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));
    }
}

