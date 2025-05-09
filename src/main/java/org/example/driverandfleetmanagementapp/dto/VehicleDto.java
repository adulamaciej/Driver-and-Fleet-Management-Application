package org.example.driverandfleetmanagementapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleDto {

    private Long id;

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Z0-9]{5,10}$", message = "Invalid license plate format")
    private String licensePlate;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Production year is required")
    @Min(value = 1920, message = "Production year must be after 1920")
    @Max(value = 2030, message = "Production year must be before 2030")
    private Integer productionYear;

    @NotNull(message = "Vehicle type is required")
    private Vehicle.VehicleType type;

    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    @NotNull(message = "Technical inspection date is required")
    @FutureOrPresent(message = "Technical inspection date must be today or in the future")
    private LocalDate technicalInspectionDate;

    @NotNull(message = "Mileage is required")
    @PositiveOrZero(message = "Mileage must be positive or zero")
    private Double mileage;

    @NotNull(message = "Status is required")
    private Vehicle.VehicleStatus status;

    private DriverBasicDto driver;
}