package in.projectjwt.main.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.projectjwt.main.dtos.LoginUserDto;
import in.projectjwt.main.dtos.ResetPasswordRequestdto;
import in.projectjwt.main.dtos.OtpVerificationRequestdto;
import in.projectjwt.main.dtos.ProfileUpdateDto;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.InvalidOTPException;

import in.projectjwt.main.repositories.UserRepository;
import in.projectjwt.main.services.AuthenticationService;
import in.projectjwt.main.services.JwtService;
import in.projectjwt.main.services.PasswordResetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
	
	private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private UserRepository userRepo;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
  

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> validationErrors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("success", false);
            response.put("errors", validationErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // HTTP 400 Bad Request
        }

        // Proceed with registration logic
        ResponseEntity<Map<String, String>> signupResponse = authenticationService.register(user);

        // If user already exists, return conflict response
        if (signupResponse.getStatusCode() == HttpStatus.CONFLICT) {
            response.put("success", "false");
            response.put("message", signupResponse.getBody().get("message"));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 Conflict
        }

        // Successful registration
        response.put("success", "true");
        response.put("message", signupResponse.getBody().get("message"));

        // Add user details
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("fullName", user.getFullName());
        userDetails.put("email", user.getEmail());
        response.put("user", userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(response); // HTTP 201 Created
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(
            @Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult) {

        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorResponse.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Proceed with authentication
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", jwtToken);  // Provide token for further use
        response.put("user", authenticatedUser);  // Include user details if needed

        return ResponseEntity.ok(response);
    }
 // Verify JWT token and get user details
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userRepo.findById(userId).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
    	try {
    		passwordResetService.sendResetLink(request.getEmail());
    		return ResponseEntity.ok("Password reset email sent successfully");
    	}
    	catch (InvalidOTPException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
        
    }
    
    @PostMapping("/otp-verification")
    public ResponseEntity<?> verifyOTP(@RequestBody OtpVerificationRequestdto request) {
        try {
        	passwordResetService.verifyOTP(request.getEmail(), request.getOtp()); // Implement this method in your UserService
            return ResponseEntity.ok("OTP verified successfully");
        } catch (InvalidOTPException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OTP verification failed: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestdto request) {
        try {
        	passwordResetService.resetPassword(request);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    @PutMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody ProfileUpdateDto request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null at controller");
        }

        // Call service method to update user profile
        User updatedUser = authenticationService.updateUserProfile(request.getId(), request.getFullName(), request.getAddress(), request.getPhone());

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User profile updated successfully");

        // Add updated user details to response
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("fullName", updatedUser.getFullName());
        userDetails.put("email", updatedUser.getEmail());
        userDetails.put("address", updatedUser.getAddress());
        userDetails.put("phoneNumber", updatedUser.getPhone());
//        userDetails.put("photoUrl", updatedUser.getPhotoUrl());
        response.put("user", userDetails);

        return ResponseEntity.ok(response);
    }

}
