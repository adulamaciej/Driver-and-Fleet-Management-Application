package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.ResourceConflictException;
import org.example.driverandfleetmanagementapp.exception.ResourceNotFoundException;
import org.example.driverandfleetmanagementapp.mapper.DriverMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver driver;
    private DriverDto driverDto;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        driver = Driver.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1985, 1, 15))
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new ArrayList<>())
                .build();

        driverDto = DriverDto.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .build();

        vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC12345")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAllDrivers_ShouldReturnPageOfDriverDtos() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findAll(any(Pageable.class))).thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<DriverDto> result = driverService.getAllDrivers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(driverDto);
    }


    @Test
    void getDriverById_WhenDriverExists_ShouldReturnDriverDto() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverById(1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void getDriverById_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getDriverById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDriverByLicenseNumber_WhenDriverExists_ShouldReturnDriverDto() {
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverByLicenseNumber("123456789");

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void getDriversByStatus_ShouldReturnPageOfDriverDtos() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findByStatus(any(Driver.DriverStatus.class), any(Pageable.class))).thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<DriverDto> result = driverService.getDriversByStatus(Driver.DriverStatus.ACTIVE, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(driverDto);
    }

    @Test
    void getDriversByName_ShouldReturnDriverDtos() {
        when(driverRepository.findByFirstNameAndLastName("John","Doe"))
                .thenReturn(List.of(driver));

        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        List<DriverDto> result = driverService.getDriversByName("John", "Doe");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(driverDto);
    }

    @Test
    void getDriversByLicenseType_ShouldReturnPageOfDriverDtos() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findByLicenseType(any(Driver.LicenseType.class), any(Pageable.class)))
                .thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<DriverDto> result = driverService.getDriversByLicenseType(Driver.LicenseType.B, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(driverDto);
    }

    @Test
    void updateDriverStatus_WhenDriverExists_ShouldReturnUpdatedDriverDto() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriverStatus(1, Driver.DriverStatus.SUSPENDED);

        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getStatus()).isEqualTo(Driver.DriverStatus.SUSPENDED);
        verify(driverRepository).save(driver);
    }

    @Test
    void updateDriverStatus_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.updateDriverStatus(1, Driver.DriverStatus.SUSPENDED))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void createDriver_WithUniqueData_ShouldReturnDriverDto() {
        when(driverRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(driverMapper.toEntity(driverDto)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.createDriver(driverDto);

        assertThat(result).isEqualTo(driverDto);
        verify(driverRepository).save(driver);
    }

    @Test
    void createDriver_WithExistingLicenseNumber_ShouldThrowException() {
        when(driverRepository.findByLicenseNumber(anyString())).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.createDriver(driverDto))
                .isInstanceOf(ResourceConflictException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void updateDriver_WhenDriverExistsAndLicenseNumberUnique_ShouldReturnUpdatedDriverDto() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriver(1, driverDto);

        assertThat(result).isEqualTo(driverDto);
        verify(driverMapper).updateDriverFromDto(driverDto, driver);
        verify(driverRepository).save(driver);
    }

    @Test
    void updateDriver_WhenLicenseNumberConflict_ShouldThrowException() {
        Driver existingDriver = Driver.builder()
                .id(2)
                .licenseNumber("123456789")
                .build();

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(existingDriver));

        assertThatThrownBy(() -> driverService.updateDriver(1, driverDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already in use");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void deleteDriver_WhenDriverExistsAndHasNoVehicles_ShouldDeleteDriver() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.existsByDriverId(1)).thenReturn(false);

        driverService.deleteDriver(1);

        verify(driverRepository).delete(driver);
    }

    @Test
    void deleteDriver_WhenDriverHasAssignedVehicles_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.existsByDriverId(1)).thenReturn(true);

        assertThatThrownBy(() -> driverService.deleteDriver(1))
                .isInstanceOf(BusinessLogicException.class);

        verify(driverRepository, never()).delete(any());
    }

    @Test
    void assignVehicleToDriver_WhenValidAssignment_ShouldReturnUpdatedDriverDto() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getVehicles()).contains(vehicle);
        verify(driverRepository).save(driver);
    }

    @Test
    void assignVehicleToDriver_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenVehicleAlreadyAssignedToAnotherDriver_ShouldThrowException() {
        Driver otherDriver = Driver.builder().id(2).build();
        vehicle.setDriver(otherDriver);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("Vehicle is already assigned to a driver");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenVehicleIsOutOfOrder_ShouldThrowException() {
        vehicle.setStatus(Vehicle.VehicleStatus.OUT_OF_ORDER);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenDriverHasIncompatibleLicenseType_ShouldThrowException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.TRUCK);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Driver's license type");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenDriverIsSuspended_ShouldThrowException() {
        driver.setStatus(Driver.DriverStatus.SUSPENDED);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Cannot assign vehicle to suspended driver");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void removeVehicleFromDriver_WhenVehicleAssignedToDriver_ShouldReturnUpdatedDriverDto() {
        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.removeVehicleFromDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getVehicles()).isEmpty();
        verify(driverRepository).save(driver);
    }

    @Test
    void removeVehicleFromDriver_WhenVehicleNotAssignedToDriver_ShouldThrowException() {

        vehicle.setDriver(null);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.removeVehicleFromDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Vehicle is not assigned to this driver");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenDriverHasMaxVehicles_ShouldThrowException() {
        Vehicle vehicle1 = Vehicle.builder().id(1).build();
        Vehicle vehicle2 = Vehicle.builder().id(2).build();
        List<Vehicle> existingVehicles = new ArrayList<>();
        existingVehicles.add(vehicle1);
        existingVehicles.add(vehicle2);

        driver.setVehicles(existingVehicles);

        Vehicle newVehicle = Vehicle.builder().id(3).build();

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(3)).thenReturn(Optional.of(newVehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 3))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("You cannot have more than 2 vehicles assigned");

        verify(driverRepository, never()).save(any());
    }
}