package org.example.driverandfleetmanagementapp.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class DriverDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDriverDto_ShouldHaveNoViolations() {
        DriverDto driverDto = DriverDto.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankFirstName_ShouldHaveViolations() {
        DriverDto driverDto = DriverDto.builder()
                .firstName("")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
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
        DriverDto driverDto = DriverDto.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("License number must be exactly 9 digits");
    }

    @Test
    void futureDateOfBirth_ShouldHaveViolation() {
        DriverDto driverDto = DriverDto.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.now().plusDays(1))
                .phoneNumber("123456789")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Date of birth must be in the past");
    }

    @Test
    void invalidEmail_ShouldHaveViolation() {
        DriverDto driverDto = DriverDto.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("invalid-email")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        Set<ConstraintViolation<DriverDto>> violations = validator.validate(driverDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Invalid email format");
    }
}