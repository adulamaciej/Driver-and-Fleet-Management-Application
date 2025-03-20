package org.example.driverandfleetmanagementapp.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VehicleDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validVehicleDto_ShouldHaveNoViolations() {
        VehicleDto vehicleDto = VehicleDto.builder()
                .brand("Toyota")
                .model("Corolla")
                .licensePlate("ABC123")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.now().plusDays(1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .productionYear(2020)
                .build();

        Set<ConstraintViolation<VehicleDto>> violations = validator.validate(vehicleDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankBrand_ShouldHaveViolation() {
        VehicleDto vehicleDto = VehicleDto.builder()
                .brand("")
                .model("Corolla")
                .licensePlate("ABC123")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.now().plusDays(1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .productionYear(2020)
                .build();

        Set<ConstraintViolation<VehicleDto>> violations = validator.validate(vehicleDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Brand is required");
    }

    @Test
    void invalidLicensePlate_ShouldHaveViolation() {
        VehicleDto vehicleDto = VehicleDto.builder()
                .brand("Toyota")
                .model("Corolla")
                .licensePlate("A")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.now().plusDays(1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .productionYear(2020)
                .build();

        Set<ConstraintViolation<VehicleDto>> violations = validator.validate(vehicleDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Invalid license plate format");
    }

    @Test
    void pastTechnicalInspectionDate_ShouldHaveViolation() {
        VehicleDto vehicleDto = VehicleDto.builder()
                .brand("Toyota")
                .model("Corolla")
                .licensePlate("ABC123")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.now().minusDays(1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .productionYear(2020)
                .build();

        Set<ConstraintViolation<VehicleDto>> violations = validator.validate(vehicleDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Technical inspection date must be in the future");
    }

    @Test
    void negativeMileage_ShouldHaveViolation() {
        VehicleDto vehicleDto = VehicleDto.builder()
                .brand("Toyota")
                .model("Corolla")
                .licensePlate("ABC123")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.now().plusDays(1))
                .mileage(-100.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .productionYear(2020)
                .build();

        Set<ConstraintViolation<VehicleDto>> violations = validator.validate(vehicleDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Mileage must be positive or zero");
    }
}