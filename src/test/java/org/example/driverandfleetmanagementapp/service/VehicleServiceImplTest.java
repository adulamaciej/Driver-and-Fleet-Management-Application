package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.mapper.VehicleMapper;
import org.example.driverandfleetmanagementapp.service.vehicle.VehicleServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
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
import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
                .id(1L)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .mileage(15000.0)
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .build();

        vehicleDto = VehicleDto.builder()
                .id(1L)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .build();

        driver = Driver.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .licenseType(Driver.LicenseType.B)
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new HashSet<>())
                .build();
    }

    @Test
    void getAllVehicles_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findAll(any(Pageable.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VehicleDto> result = vehicleService.getAllVehicles(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleById_WhenVehicleExists_ShouldReturnVehicleDto() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleById(1L);
        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleById_WhenVehicleDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getVehicleByLicensePlate_WhenVehicleExists_ShouldReturnVehicleDto() {
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleByLicensePlate("ABC123");

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleByLicensePlate_WhenVehicleDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleByLicensePlate("ABC123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getVehiclesByStatus_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByStatus(any(Vehicle.VehicleStatus.class), any(Pageable.class)))
                .thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VehicleDto> result = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByBrandAndModel_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByBrandAndModel(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VehicleDto> result = vehicleService.getVehiclesByBrandAndModel("Toyota", "Corolla", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByType_ShouldReturnPageOfVehicleDtos() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findByType(any(Vehicle.VehicleType.class), any(Pageable.class)))
                .thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<VehicleDto> result = vehicleService.getVehiclesByType(Vehicle.VehicleType.CAR, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByDriverId_WhenDriverExists_ShouldReturnVehicleDtoList() {
        Set<Vehicle> vehicles = Set.of(vehicle);
        when(vehicleRepository.findByDriverId(1L)).thenReturn(vehicles);
        when(vehicleMapper.toDtoSet(vehicles)).thenReturn(Set.of(vehicleDto));

        when(driverRepository.existsById(1L)).thenReturn(true);
        Set<VehicleDto> result = vehicleService.getVehiclesByDriverId(1L);

        assertThat(result).hasSize(1);
        assertThat(result).contains(vehicleDto);
    }

    @Test
    void getVehiclesByDriverId_WhenDriverDoesNotExist_ShouldThrowException() {
        when(driverRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.getVehiclesByDriverId(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(driverRepository).existsById(1L);
        verify(vehicleRepository, never()).findByDriverId(anyLong());
    }

    @Test
    void createVehicle_WhenLicensePlateUnique_ShouldReturnVehicleDto() {
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void createVehicle_WhenLicensePlateExists_ShouldThrowException() {
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.createVehicle(vehicleDto))
                .isInstanceOf(ResourceConflictException.class);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void createVehicle_WithExpiredInspection_ShouldThrowException() {
        VehicleDto expiredVehicleDto = vehicleDto.toBuilder()
                .technicalInspectionDate(LocalDate.now().minusDays(1))
                .build();

        assertThatThrownBy(() -> vehicleService.createVehicle(expiredVehicleDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Technical inspection has expired");

        verify(vehicleRepository, never()).save(any());
    }


    @Test
    void updateVehicle_WhenLicensePlateConflict_ShouldThrowException() {
        Vehicle existingVehicle = Vehicle.builder().id(2L).licensePlate("ABC123").build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(existingVehicle));

        assertThatThrownBy(() -> vehicleService.updateVehicle(1L, vehicleDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already in use");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void deleteVehicle_WhenVehicleExistsAndNotInUse_ShouldDeleteVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_WhenVehicleInUse_ShouldThrowException() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.deleteVehicle(1L))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("assigned to driver");

        verify(vehicleRepository, never()).delete(any());
    }


    @Test
    void updateVehicleMileage_WhenValidMileage_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleMileage(1L, 20000.0);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getMileage()).isEqualTo(20000.0);
    }


    @Test
    void updateVehicleStatus_ShouldReturnUpdatedVehicleDto() {
        vehicle.setDriver(driver);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1L, Vehicle.VehicleStatus.IN_USE);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_USE);
    }

}