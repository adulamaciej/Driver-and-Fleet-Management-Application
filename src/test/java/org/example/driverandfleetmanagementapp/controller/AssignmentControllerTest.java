package org.example.driverandfleetmanagementapp.controller;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.assignment.AssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AssignmentControllerTest {

    @Mock
    private AssignmentService assignmentService;

    @InjectMocks
    private AssignmentController assignmentController;

    private DriverDto driverDto;
    private VehicleDto vehicleDto;

    @BeforeEach
    void setUp() {
        driverDto = DriverDto.builder()
                .id(7L)
                .firstName("Adam")
                .lastName("Nowak")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1985, 1, 15))
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        vehicleDto = VehicleDto.builder()
                .id(13L)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.now())
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .productionYear(2020)
                .mileage(0.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void assignVehicleToDriver_ShouldReturnUpdatedDriver() {

        //Given
        Long driverId = driverDto.getId();
        Long vehicleId = vehicleDto.getId();

        when(assignmentService.assignVehicleToDriver(driverId, vehicleId)).thenReturn(driverDto);

        // When
        ResponseEntity<DriverDto> response = assignmentController.assignVehicleToDriver(driverId, vehicleId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(assignmentService).assignVehicleToDriver(driverId, vehicleId);
    }

    @Test
    void removeVehicleFromDriver_ShouldReturnUpdatedDriver() {
        // Given
        Long driverId = driverDto.getId();
        Long vehicleId = vehicleDto.getId();

        when(assignmentService.removeVehicleFromDriver(driverId, vehicleId)).thenReturn(driverDto);

        // When
        ResponseEntity<DriverDto> response = assignmentController.removeVehicleFromDriver(driverId, vehicleId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(assignmentService).removeVehicleFromDriver(driverId, vehicleId);
    }

}