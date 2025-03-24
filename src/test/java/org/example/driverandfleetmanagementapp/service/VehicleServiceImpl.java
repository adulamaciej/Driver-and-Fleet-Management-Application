package org.example.driverandfleetmanagementapp.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.ResourceConflictException;
import org.example.driverandfleetmanagementapp.mapper.VehicleMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private VehicleDto vehicleDto;
    private Driver driver;

    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC12345")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        vehicleDto = VehicleDto.builder()
                .id(1)
                .licensePlate("ABC12345")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        driver = Driver.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new ArrayList<>())
                .build();
    }

    @Test
    void getAllVehicles_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findAll(any(Pageable.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(any(Vehicle.class))).thenReturn(vehicleDto);

        Page<VehicleDto> result = vehicleService.getAllVehicles(0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void getVehicleById_WhenVehicleExists_ShouldReturnVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleById(1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleByLicensePlate_WhenVehicleExists_ShouldReturnVehicleDto() {
        when(vehicleRepository.findByLicensePlate("ABC12345")).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleByLicensePlate("ABC12345");

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByStatus_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByStatus(any(Vehicle.VehicleStatus.class), any(Pageable.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Page<VehicleDto> result = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void getVehiclesByBrandAndModel_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByBrandAndModel(anyString(), anyString(), any(Pageable.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Page<VehicleDto> result = vehicleService.getVehiclesByBrandAndModel("Toyota", "Corolla", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void getVehiclesByType_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByType(any(Vehicle.VehicleType.class), any(Pageable.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Page<VehicleDto> result = vehicleService.getVehiclesByType(Vehicle.VehicleType.CAR, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(vehicleMapper).toDto(vehicle);
    }

    @Test
    void getVehiclesByDriverId_ShouldReturnListOfVehicleDtos() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findByDriverId(1)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtoList(anyList())).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByDriverId(1);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void createVehicle_WithUniqueData_ShouldReturnVehicleDto() {
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void createVehicle_WithExistingLicensePlate_ShouldThrowException() {
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.createVehicle(vehicleDto))
                .isInstanceOf(ResourceConflictException.class);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void createVehicle_WithDriverData_ShouldCreateVehicleWithDriver() {
        vehicleDto = vehicleDto.toBuilder()
                .driver(org.example.driverandfleetmanagementapp.dto.DriverBasicDto.builder()
                        .id(1)
                        .build())
                .build();

        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getDriver()).isEqualTo(driver);
    }

    @Test
    void updateVehicle_WhenVehicleExistsAndLicensePlateUnique_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicle(1, vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
        verify(vehicleMapper).updateVehicleFromDto(vehicleDto, vehicle);
    }

    @Test
    void updateVehicle_WhenLicensePlateConflict_ShouldThrowException() {
        Vehicle existingVehicle = Vehicle.builder()
                .id(2)
                .licensePlate("ABC12345")
                .build();

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate("ABC12345")).thenReturn(Optional.of(existingVehicle));

        assertThatThrownBy(() -> vehicleService.updateVehicle(1, vehicleDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("License plate ABC12345 already in use");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicle_WithDriverData_ShouldUpdateDriver() {
        vehicleDto = vehicleDto.toBuilder()
                .driver(org.example.driverandfleetmanagementapp.dto.DriverBasicDto.builder()
                        .id(1)
                        .firstName("John")
                        .lastName("Doe")
                        .build())
                .build();

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicle(1, vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getDriver()).isEqualTo(driver);
    }

    @Test
    void deleteVehicle_WhenVehicleExistsAndNotInUse_ShouldDeleteVehicle() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_WhenVehicleInUseByDriver_ShouldThrowException() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.deleteVehicle(1))
                .isInstanceOf(BusinessLogicException.class);

        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void deleteVehicle_WhenVehicleHasDriverButNotInUse_ShouldRemoveDriverAndDelete() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        driver.setVehicles(new ArrayList<>(List.of(vehicle)));

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1);

        verify(driverRepository).save(driver);
        verify(vehicleRepository).delete(vehicle);
        assertThat(vehicle.getDriver()).isNull();
        assertThat(driver.getVehicles()).isEmpty();
    }

    @Test
    void assignVehicleToDriver_WhenValidAssignment_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getDriver()).isEqualTo(driver);
    }


    @Test
    void assignVehicleToDriver_WhenVehicleIsOutOfOrder_ShouldThrowException() {
        vehicle.setStatus(Vehicle.VehicleStatus.OUT_OF_ORDER);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenInvalidLicenseType_ShouldThrowException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.TRUCK);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Driver's license type");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenVehicleAlreadyAssigned_ShouldThrowException() {

        Driver otherDriver = Driver.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .build();
        vehicle.setDriver(otherDriver);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("Vehicle is already assigned to a driver");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignVehicleToDriver_WhenDriverIsSuspended_ShouldThrowException() {
        driver.setStatus(Driver.DriverStatus.SUSPENDED);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Cannot assign vehicle to suspended driver");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void removeDriverFromVehicle_WhenVehicleHasDriver_ShouldReturnUpdatedVehicleDto() {
        vehicle.setDriver(driver);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.removeDriverFromVehicle(1);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getDriver()).isNull();
    }
    @Test
    void removeDriverFromVehicle_WhenVehicleHasNoDriver_ShouldThrowException() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.removeDriverFromVehicle(1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Vehicle has no assigned driver");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicleMileage_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleMileage(1, 15000.0);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getMileage()).isEqualTo(15000.0);
    }

    @Test
    void updateVehicleStatus_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1, Vehicle.VehicleStatus.IN_SERVICE);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_SERVICE);
    }

}