package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.mapper.DriverMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.example.driverandfleetmanagementapp.service.assignment.AssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AssignmentServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Driver driver;
    private Vehicle vehicle;
    private DriverDto driverDto;

    @BeforeEach
    void setUp() {

        driver = Driver.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new LinkedHashSet<>())
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 15))
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .mileage(15000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        driverDto = DriverDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void assignVehicleToDriver_WhenValidAssignment_ShouldReturnUpdatedDriverDto() {
        // Arrange
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        // Act
        DriverDto result = assignmentService.assignVehicleToDriver(1L, 1L);

        // Assert
        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getVehicles()).contains(vehicle);
        assertThat(vehicle.getDriver()).isEqualTo(driver);
        assertThat(vehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_USE);

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
        verify(driverMapper).toDto(driver);
    }


    @Test
    void assignVehicleToDriver_WhenVehicleNotAvailable_ShouldThrowException() {
        // Arrange
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.assignVehicleToDriver(1L, 1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Cannot assign vehicle with status IN_USE to a driver");

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
    }


    @Test
    void assignVehicleToDriver_WhenDriverNotActive_ShouldThrowException() {
        // Arrange
        driver.setStatus(Driver.DriverStatus.SUSPENDED);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.assignVehicleToDriver(1L, 1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Cannot assign vehicle to driver with status SUSPENDED");

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
    }


    @Test
    void assignVehicleToDriver_WhenVehicleAlreadyAssigned_ShouldThrowException() {
        // Arrange
        Driver otherDriver = Driver.builder().id(2L).build();
        vehicle.setDriver(otherDriver);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.assignVehicleToDriver(1L, 1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("Vehicle is already assigned to a driver");

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
    }


    @Test
    void removeVehicleFromDriver_WhenValidRemoval_ShouldReturnUpdatedDriverDto() {
        // Arrange
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        driver.getVehicles().add(vehicle);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        // Act
        DriverDto result = assignmentService.removeVehicleFromDriver(1L, 1L);

        // Assert
        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getVehicles()).isEmpty();
        assertThat(vehicle.getDriver()).isNull();
        assertThat(vehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.AVAILABLE);

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
        verify(driverMapper).toDto(driver);
    }


    @Test
    void removeVehicleFromDriver_WhenVehicleAssignedToDifferentDriver_ShouldThrowException() {
        // Arrange
        Driver otherDriver = Driver.builder().id(2L).build();
        vehicle.setDriver(otherDriver);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> assignmentService.removeVehicleFromDriver(1L, 1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Vehicle is assigned to a different driver");

        verify(driverRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
    }
}
