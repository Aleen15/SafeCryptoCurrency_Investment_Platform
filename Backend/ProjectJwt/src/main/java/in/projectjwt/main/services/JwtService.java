package in.projectjwt.main.services;

import org.springframework.stereotype.Service;

import in.projectjwt.main.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;


@Service
public class JwtService {
	
	@Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
   
 // New Method: Generate token for Password Reset Flow
    public String generateResetPasswordToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset_password_started", true);  // Custom claim for reset password start
        return buildToken(claims, new User(), jwtExpiration); // Generate token with custom claim
    }

    // New Method: Generate token for OTP Verification
    public String generateOtpVerifiedToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset_password_started", true);  // Ensure reset process started
        claims.put("otp_verified", true);            // Custom claim for OTP verification
        return buildToken(claims, new User(), jwtExpiration);
    }

    // New Method: Generate token for Password Change
    public String generatePasswordChangeToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset_password_started", true);  // Ensure reset process started
        claims.put("otp_verified", true);            // Ensure OTP is verified
        return buildToken(claims, new User(), jwtExpiration);
    }

    // New Method: Validate the custom claims for password reset flow
    public boolean isValidPasswordResetToken(String token, String email) {
        final Claims claims = extractAllClaims(token);
        return email.equals(claims.getSubject()) &&
               claims.get("reset_password_started", Boolean.class) != null &&
               claims.get("otp_verified", Boolean.class) != null;
    }



}
