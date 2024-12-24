package in.projectjwt.main.exceptions;

public class LoginResponse {
    private String token;
    private String expiresIn;
    private String error;

    // Getters and Setters

    public String getError() {
        return error;
    }

    public LoginResponse setError(String error) {
        this.error = error;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
