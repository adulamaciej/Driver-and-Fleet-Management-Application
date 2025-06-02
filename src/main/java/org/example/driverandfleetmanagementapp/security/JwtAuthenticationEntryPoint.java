package org.example.driverandfleetmanagementapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.driverandfleetmanagementapp.exception.custom.JwtAuthenticationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import org.example.driverandfleetmanagementapp.exception.ErrorResponse;
import org.springframework.http.HttpStatus;



@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .localDateTime(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(determineErrorMessage(request, authException ))
                .path("uri=" + request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }


    private String determineErrorMessage(HttpServletRequest request, AuthenticationException authException) {
        String authHeader = request.getHeader("Authorization");

        if (authException instanceof JwtAuthenticationException) {
            return authException.getMessage();
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Missing or invalid Authorization header. Please provide 'Bearer <token>'";
        }

        return "Authentication failed. Please check your token and try again.";
    }
}


