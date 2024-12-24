package in.projectjwt.main.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginUserDto {
	 @NotBlank(message = "Email is required!!")
	    @Pattern(
	    	    regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$",
	    	    message = "Email must contain @ and domain name..."
	    	)
	    @Column(unique = true, nullable = false)
	    private String email;
	    @NotBlank(message = "Password is required!!")
	    @Column(nullable = false)
	    private String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    

}
