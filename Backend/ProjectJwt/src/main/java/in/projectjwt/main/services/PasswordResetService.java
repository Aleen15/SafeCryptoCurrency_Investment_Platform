package in.projectjwt.main.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import in.projectjwt.main.exceptions.InvalidOTPException;

import in.projectjwt.main.dtos.ResetPasswordRequestdto;
import in.projectjwt.main.entities.PasswordResetState;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.repositories.UserRepository;

@Service
public class PasswordResetService {
	@Autowired
    private UserRepository userRepository;
	
	

    @Autowired
    private EmailService emailService; // Implement email sending logic

    public String sendResetLink(String email) {
        System.out.println("Received email: " + email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user = userOpt.orElseThrow(() -> new InvalidOTPException("User with email " + email + " not found"));
        user.setResetState(PasswordResetState.RESET_REQUESTED);
        // Generate OTP
        String otp = generateOTP();

        // Set the OTP and timestamp on the user
        user.setOtp(otp);
        user.setOtpTimestamp(new Date());

        // Save the user with the OTP and timestamp
        userRepository.save(user);

        // Send OTP email
        emailService.sendOtpEmail(user.getEmail(), otp);
        user.setResetState(PasswordResetState.EMAIL_VERIFIED);

        return "Password reset link sent to " + email;
    }


//    public String sendResetLink(String email) {
//    	System.out.println("Received email: " + email);
//    	// Check if the user is found
//    	Optional<User> userOpt = userRepository.findByEmail(email);
//        User user = userOpt.orElseThrow(() -> new InvalidOTPException("User with email " + email + " not found"));
//
//        // Generate OTP
//        String otp = generateOTP();
//
//        // Set the OTP on the user
//        user.setOtp(otp);
//        
//        // Save the user with the OTP set
//        userRepository.save(user);
//
//        // Send OTP email
//        emailService.sendOtpEmail(user.getEmail(), otp);
//
//        // Optionally return some confirmation or message if required
//        return "Password reset link sent to " + email;
//}
    	

    	
//        if (user.isEmpty()) {
//            throw new OTPExpiredException("User with email " + email + " not found");
//        }
//
//        String otp = generateOTP();
//        user.setOtp(otp);
//        userRepository.save(user);
//
//        emailService.sendOtpEmail(user.getEmail(), otp);
        //------------------------------
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Email not found"));
//
//        String token = UUID.randomUUID().toString();
//        resetTokens.put(token, email);
//
//        // Send email with reset link
//        String resetLink = "http://localhost:3000/auth/reset-password?token=" + token;
//        emailService.sendEmail(email, "Password Reset Request", "Click the link to reset: " + resetLink);
//
//        return "Password reset link sent to your email.";
  
    private String generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    



//    public String resetPassword(String token, String newPassword) {
//        if (!resetTokens.containsKey(token)) {
//            throw new RuntimeException("Invalid or expired token");
//        }
//
//        String email = resetTokens.get(token);
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt())); // Hash new password
//        userRepository.save(user);
//        resetTokens.remove(token); // Invalidate token
//        return "Password updated successfully.";
//    }
//
//	public void verifyOTP(String email, String otp) {
//		// TODO Auto-generated method stub
//		Optional<User> userOpt = userRepository.findByEmail(email);
//		// Check if user is present
//	    User user = userOpt.orElseThrow(() -> new InvalidOTPException("User with email " + email + " not found"));
//
//	    // Validate OTP
//	    if (!user.getOtp().equals(otp)) {
//	        throw new InvalidOTPException("Invalid OTP");
//	    }
//	}
	public void verifyOTP(String email, String otp) {
	    Optional<User> userOpt = userRepository.findByEmail(email);
	    User user = userOpt.orElseThrow(() -> new InvalidOTPException("User with email " + email + " not found"));
	    if (user.getResetState() != PasswordResetState.EMAIL_VERIFIED) {
	        throw new InvalidOTPException("Email verification is required before OTP verification.");
	    }
	    // Check if OTP is expired
	    long otpExpiryTime = 15 * 60 * 1000; // e.g., 15 minutes
	    long otpTimestamp = user.getOtpTimestamp().getTime();
	    long currentTime = System.currentTimeMillis();

	    if (currentTime - otpTimestamp > otpExpiryTime) {
	        throw new InvalidOTPException("OTP has expired");
	    }

	    // Validate OTP format and value
	    if (!user.getOtp().equals(otp.trim())) {
	        throw new InvalidOTPException("Invalid OTP");
	    }

	    // Optionally clear the OTP after successful verification (to prevent reuse)
	    user.setOtp(null);
	    user.setOtpTimestamp(null);
	    user.setResetState(PasswordResetState.OTP_VERIFIED);
	    userRepository.save(user);
	}

//        if (userOpt.isEmpty()) {
//            throw new OTPExpiredException("User with email " + email + " not found");
//        }
//
//        if (!user.getOtp().equals(otp)) {
//            throw new OTPExpiredException("Invalid OTP");
//        }

		
	

	public void resetPassword(ResetPasswordRequestdto request) {
		// TODO Auto-generated method stub
		Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
		User user = userOpt.orElseThrow(() -> new InvalidOTPException("User with email " + request.getEmail() + " not found"));
		if (user.getResetState() != PasswordResetState.OTP_VERIFIED) {
	        throw new InvalidOTPException("OTP verification is required before changing the password.");
	    }
	    // Update user's password
	    user.setPassword(request.getNewPassword());
	    
	    // Save the updated user object to the repository
	    user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
	 
	    user.setResetState(PasswordResetState.PASSWORD_CHANGED);
	    userRepository.save(user);
//        if (user.isEmpty()) {
//            throw new OTPExpiredException("User with email " + request.getEmail() + " not found");
//        }
//
//        // Update user's password
//        user.setPassword(request.getNewPassword());
//        userRepository.save(user);
		
	}

}
