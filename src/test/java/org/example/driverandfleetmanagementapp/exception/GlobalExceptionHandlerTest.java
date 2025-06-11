package org.example.driverandfleetmanagementapp.exception;


import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    private static final String TEST_PATH = "uri=/api/drivers/1";

    @BeforeEach
    void setUp() {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/api/drivers/1");
        when(webRequest.getDescription(false)).thenReturn(TEST_PATH);
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundStatus() {
        String errorMessage = "Driver with ID 1 not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        verifyErrorResponse(response, HttpStatus.NOT_FOUND, errorMessage, true);
    }

    @Test
    void handleResourceConflictException_ShouldReturnConflictStatus() {
        String errorMessage = "Driver with license number 123456789 already exists";
        ResourceConflictException exception = new ResourceConflictException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceConflictException(exception, webRequest);

        verifyErrorResponse(response, HttpStatus.CONFLICT, errorMessage, true);
    }

    @Test
    void handleBusinessLogicException_ShouldReturnBadRequestStatus() {
        String errorMessage = "Cannot assign vehicle to suspended driver";
        BusinessLogicException exception = new BusinessLogicException(errorMessage);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessLogicException(exception, webRequest);

        verifyErrorResponse(response, HttpStatus.BAD_REQUEST, errorMessage, true);
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequestStatus() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(webRequest);
        verifyErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid input data. Please check your request.", true);
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorizedStatus() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(webRequest);
        verifyErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid username or password", true);
    }

    @ParameterizedTest
    @MethodSource("provideExceptionsForGlobalHandler")
    void handleGlobalExceptions_ShouldReturnInternalServerErrorStatus(Exception exception, boolean includePath) {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(webRequest);

        verifyErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", includePath);
    }

    private static Stream<Arguments> provideExceptionsForGlobalHandler() {
        return Stream.of(
                Arguments.of(new Exception("Unexpected database error"), true),
                Arguments.of(new RuntimeException("Unexpected runtime error"), true),
                Arguments.of(new NullPointerException("Null reference"), false),
                Arguments.of(new IllegalArgumentException("Invalid argument provided"), false)
        );
    }

    private void verifyErrorResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus,
                                     String expectedMessage, boolean verifyPath) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(expectedStatus.value());
        assertThat(errorResponse.getError()).isEqualTo(expectedStatus.getReasonPhrase());
        assertThat(errorResponse.getMessage()).startsWith(expectedMessage);

        if (verifyPath) {
            assertThat(errorResponse.getPath()).isEqualTo(TEST_PATH);
        }

        assertThat(errorResponse.getLocalDateTime()).isNotNull();
        assertThat(errorResponse.getLocalDateTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}

