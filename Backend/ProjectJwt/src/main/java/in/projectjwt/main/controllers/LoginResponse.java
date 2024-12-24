package in.projectjwt.main.controllers;

import in.projectjwt.main.dtos.LoginUserDto;

public class LoginResponse {
	private String token;

    private long expiresIn;
    
    private String email;
    private String fullname;
    private String succes;

    public String getEmail() {
		return email;
	}

	public LoginResponse setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFullname() {
		return fullname;
	}

	public LoginResponse setFullname(String fullname) {
		this.fullname = fullname;
		return this;
	}

	public String getToken() {
        return token;
    }

	public long getExpiresIn() {
		return expiresIn;
	}

	public LoginResponse setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
        return this; 
	}

	public LoginResponse setToken(String token) {
		this.token = token;
        return this; 
	}

	

}
