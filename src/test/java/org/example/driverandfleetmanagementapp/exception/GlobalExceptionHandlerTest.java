package org.example.driverandfleetmanagementapp.exception;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/drivers/1");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/drivers/1");
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {

        String errorMessage = "Driver with ID 1 not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
        assertThat(errorResponse.getLocalDateTime()).isNotNull();
        assertThat(errorResponse.getLocalDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleResourceConflictException_ShouldReturnConflictStatus() {

        String errorMessage = "Driver with license number 123456789 already exists";
        ResourceConflictException exception = new ResourceConflictException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceConflictException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.CONFLICT.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
        assertThat(errorResponse.getLocalDateTime()).isNotNull();
    }

    @Test
    void handleBusinessLogicException_ShouldReturnBadRequestStatus() {

        String errorMessage = "Cannot assign vehicle to suspended driver";
        BusinessLogicException exception = new BusinessLogicException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessLogicException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
        assertThat(errorResponse.getLocalDateTime()).isNotNull();
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatus() {

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
        assertThat(errorResponse.getLocalDateTime()).isNotNull();
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorStatus() {

        Exception exception = new Exception("Unexpected database error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo("Unexpected error occurred");
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
        assertThat(errorResponse.getLocalDateTime()).isNotNull();
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorStatus() {

        RuntimeException exception = new RuntimeException("Unexpected runtime error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo("Unexpected error occurred");
        assertThat(errorResponse.getPath()).isEqualTo("uri=/api/drivers/1");
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerErrorStatus() {

        NullPointerException exception = new NullPointerException("Null reference");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo("Unexpected error occurred");
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnInternalServerErrorStatus() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorResponse.getError()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(errorResponse.getMessage()).isEqualTo("Unexpected error occurred");
    }
}

