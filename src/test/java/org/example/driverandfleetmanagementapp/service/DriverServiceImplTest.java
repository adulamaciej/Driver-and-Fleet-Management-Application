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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
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
        when(driverMapper.toDto(any(Driver.class))).thenReturn(driverDto);

        Page<DriverDto> result = driverService.getAllDrivers(0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(driverMapper).toDto(driver);
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

        Page<DriverDto> result = driverService.getDriversByStatus(Driver.DriverStatus.ACTIVE, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(driverMapper).toDto(driver);
    }

    @Test
    void getDriversByName_ShouldReturnPageOfDriverDtos() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findByFirstNameAndLastName(anyString(), anyString(), any(Pageable.class))).thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Page<DriverDto> result = driverService.getDriversByName("John", "Doe", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(driverMapper).toDto(driver);
    }

    @Test
    void getDriversByLicenseType_ShouldReturnPageOfDriverDtos() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findByLicenseType(any(Driver.LicenseType.class), any(Pageable.class))).thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Page<DriverDto> result = driverService.getDriversByLicenseType(Driver.LicenseType.B, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(driverMapper).toDto(driver);
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
        assertThat(vehicle.getDriver()).isEqualTo(driver);
    }
    @Test
    void assignVehicleToDriver_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(vehicleRepository, never()).findById(any());
    }
    @Test
    void assignVehicleToDriver_WhenVehicleAlreadyAssignedToAnotherDriver_ShouldThrowException() {
        Driver otherDriver = Driver.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .build();
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
        assertThat(driver.getVehicles()).doesNotContain(vehicle);
        assertThat(vehicle.getDriver()).isNull();
    }
    @Test
    void removeVehicleFromDriver_WhenVehicleNotAssignedToDriver_ShouldThrowException() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));



        assertThatThrownBy(() -> driverService.removeVehicleFromDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Vehicle is not assigned to this driver");

        verify(driverRepository, never()).save(any());
    }

}