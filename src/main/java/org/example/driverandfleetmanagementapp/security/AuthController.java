package org.example.driverandfleetmanagementapp.security;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.security.dto.JwtResponse;
import org.example.driverandfleetmanagementapp.security.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @RateLimiter(name = "auth-api", fallbackMethod = "loginFallback")
    @PostMapping("/login")
    @Operation(summary = "Login with username and password", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
            JwtResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);

    }

    public ResponseEntity<JwtResponse> loginFallback(LoginRequest loginRequest, Exception ex) {
        return ResponseEntity.status(429)
                .body(new JwtResponse("Rate limit exceeded. Please try again later."));
    }
}