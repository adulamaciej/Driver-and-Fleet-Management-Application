package org.example.driverandfleetmanagementapp.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.driverandfleetmanagementapp.model.Driver;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverDto {

    private Integer id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    private String lastName;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^\\d{9}$", message = "License number must be exactly 9 digits")
    private String licenseNumber;

    @NotNull(message = "License type is required")
    private Driver.LicenseType licenseType;


    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{9}$", message = "Phone number must be exactly 9 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Status is required")
    private Driver.DriverStatus status;

    private List<VehicleBasicDto> vehicles;
}