package org.example.driverandfleetmanagementapp.security.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class JwtResponse {

    private final String token;

}
