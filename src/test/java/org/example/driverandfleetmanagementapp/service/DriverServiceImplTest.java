package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
import org.example.driverandfleetmanagementapp.service.driver.DriverServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.mapper.DriverMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1985, 1, 15))
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new HashSet<>())
                .build();

        driverDto = DriverDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
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
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverById(1L);
        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void getDriverById_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getDriverById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDriverByLicenseNumber_ShouldReturnDriverDto() {
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
    void getDriversByFirstAndLastName_ShouldReturnDriverDtos() {
        when(driverRepository.findByFirstNameAndLastName("John","Doe"))
                .thenReturn(List.of(driver));

        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        List<DriverDto> result = driverService.getDriversByFirstAndLastName("John", "Doe");

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
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriverStatus(1L, Driver.DriverStatus.SUSPENDED);

        assertThat(result).isEqualTo(driverDto);
        assertThat(driver.getStatus()).isEqualTo(Driver.DriverStatus.SUSPENDED);
    }

    @Test
    void updateDriverStatus_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.updateDriverStatus(1L, Driver.DriverStatus.SUSPENDED))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(driverRepository, never()).save(any());
    }

    @Test
    void createDriver_ShouldReturnDriverDto() {

        driverDto = driverDto.toBuilder()
                .status(Driver.DriverStatus.ACTIVE)
                .build();

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

        driverDto = driverDto.toBuilder()
                .status(Driver.DriverStatus.ACTIVE)
                .build();


        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriver(1L, driverDto);

        assertThat(result).isEqualTo(driverDto);
        verify(driverMapper).updateDriverFromDto(driverDto, driver);
    }

    @Test
    void updateDriver_WhenLicenseNumberConflict_ShouldThrowException() {
        Driver existingDriver = Driver.builder()
                .id(2L)
                .licenseNumber("123456789")
                .build();

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(existingDriver));

        assertThatThrownBy(() -> driverService.updateDriver(1L, driverDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already in use");

        verify(driverRepository, never()).save(any());
    }

    @Test
    void deleteDriver_WhenDriverHasAssignedVehicles_ShouldThrowException() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.existsByDriverId(1L)).thenReturn(true);

        assertThatThrownBy(() -> driverService.deleteDriver(1L))
                .isInstanceOf(BusinessLogicException.class);

        verify(driverRepository, never()).delete(any());
    }



}

