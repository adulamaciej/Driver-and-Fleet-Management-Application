package org.example.driverandfleetmanagementapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.example.driverandfleetmanagementapp.exception.custom.JwtAuthenticationException;



@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;


    private Key getSigningKey() {
        if (SECRET_KEY.length() < 32) {
            throw new IllegalArgumentException("JWT key must have minimum 32 characters!");
        }
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }


    private String createToken(Map<String, Object> claims, String subject) {
        long expirationTimeMillis = EXPIRATION_TIME * 1000;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token has expired");
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("JWT token is malformed");
        } catch (SignatureException e) {
            throw new JwtAuthenticationException("JWT token has invalid signature");
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token) {
        Claims claims = extractAllClaims(token);
        return !claims.getExpiration().before(new Date());
    }


}