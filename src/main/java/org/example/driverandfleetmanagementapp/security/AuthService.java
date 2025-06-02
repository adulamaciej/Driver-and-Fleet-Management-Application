package org.example.driverandfleetmanagementapp.security;

import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.security.dto.JwtResponse;
import org.example.driverandfleetmanagementapp.security.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String role = authentication.getAuthorities().stream()
                .iterator()
                .next()
                .getAuthority();


        String jwt = jwtUtil.generateToken(loginRequest.getUsername(), role);

        return new JwtResponse(jwt);
    }
}