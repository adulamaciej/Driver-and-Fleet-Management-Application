package org.example.driverandfleetmanagementapp.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
public class DriverDtoValidationTest {

    private static Validator validator;
    private DriverDto validDriverDto;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUpValidDriverDto() {
        validDriverDto = DriverDto.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        Set<ConstraintViolation<DriverDto>> baseViolations = validator.validate(validDriverDto);
        assertThat(baseViolations).isEmpty();
    }

    @Test
    void validDriverDto_ShouldHaveNoViolations() {
        Set<ConstraintViolation<DriverDto>> violations = validator.validate(validDriverDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankFirstName_ShouldHaveViolations() {
        DriverDto driverDto = validDriverDto.toBuilder()
                .firstName("")
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(2);
        assertThat(violations)
                .extracting("message")
                .containsExactlyInAnyOrder(
                        "First name is required",
                        "First name must be between 2 and 20 characters"
                );
    }

    @Test
    void invalidLicenseNumber_ShouldHaveViolation() {
        DriverDto driverDto = validDriverDto.toBuilder()
                .licenseNumber("123")
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("License number must be exactly 9 digits");
    }

    @Test
    void futureDateOfBirth_ShouldHaveViolation() {
        DriverDto driverDto = validDriverDto.toBuilder()
                .dateOfBirth(LocalDate.now().plusDays(1))
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Date of birth must be in the past");
    }

    @Test
    void invalidEmail_ShouldHaveViolation() {
        DriverDto driverDto = validDriverDto.toBuilder()
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Invalid email format");
    }
}