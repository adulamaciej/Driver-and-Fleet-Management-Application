package org.example.driverandfleetmanagementapp.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must have 3-30 signs")
    private final String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have minimum 6 signs")
    private final String password;
}

