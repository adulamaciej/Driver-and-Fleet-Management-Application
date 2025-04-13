package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.ResourceConflictException;
import org.example.driverandfleetmanagementapp.mapper.VehicleMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .mileage(15000.0)
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .build();

        vehicleDto = VehicleDto.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        driver = Driver.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseType(Driver.LicenseType.B)
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new ArrayList<>())
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
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleById(1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleById_WhenVehicleDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(1))
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
        List<Vehicle> vehicles = List.of(vehicle);
        when(vehicleRepository.findByDriverId(1)).thenReturn(vehicles);
        when(vehicleMapper.toDtoList(vehicles)).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByDriverId(1);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByDriverId_WhenDriverDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findByDriverId(1)).thenReturn(List.of());
        when(driverRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.getVehiclesByDriverId(1))
                .isInstanceOf(ResourceNotFoundException.class);
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
        vehicle.setTechnicalInspectionDate(LocalDate.now().minusDays(1));
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);

        assertThatThrownBy(() -> vehicleService.createVehicle(vehicleDto))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Technical inspection has expired");

        verify(vehicleRepository, never()).save(any());
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
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateVehicle_WhenLicensePlateConflict_ShouldThrowException() {
        Vehicle existingVehicle = Vehicle.builder().id(2).licensePlate("ABC123").build();

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate(anyString())).thenReturn(Optional.of(existingVehicle));

        assertThatThrownBy(() -> vehicleService.updateVehicle(1, vehicleDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already in use");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void deleteVehicle_WhenVehicleExistsAndNotInUse_ShouldDeleteVehicle() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_WhenVehicleInUse_ShouldThrowException() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.deleteVehicle(1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("currently in use");

        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void assignDriverToVehicle_WhenValidAssignment_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignDriverToVehicle(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getDriver()).isEqualTo(driver);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void assignDriverToVehicle_WhenVehicleDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignDriverToVehicle_WhenVehicleIsOutOfOrder_ShouldThrowException() {
        vehicle.setStatus(Vehicle.VehicleStatus.OUT_OF_ORDER);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("OUT_OF_ORDER");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignDriverToVehicle_WhenVehicleAlreadyHasDriver_ShouldThrowException() {
        vehicle.setDriver(driver);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already assigned");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignDriverToVehicle_WhenDriverDoesNotExist_ShouldThrowException() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignDriverToVehicle_WhenDriverIsSuspended_ShouldThrowException() {
        driver.setStatus(Driver.DriverStatus.SUSPENDED);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("suspended driver");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void assignDriverToVehicle_WhenDriverHasIncompatibleLicense_ShouldThrowException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.TRUCK);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("license type");

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
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void removeDriverFromVehicle_WhenVehicleHasNoDriver_ShouldThrowException() {
        vehicle.setDriver(null);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.removeDriverFromVehicle(1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("no assigned driver");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicleMileage_WhenValidMileage_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleMileage(1, 20000.0);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getMileage()).isEqualTo(20000.0);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateVehicleMileage_WhenNegativeMileage_ShouldThrowException() {
        assertThatThrownBy(() -> vehicleService.updateVehicleMileage(1, -100.0))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("negative");

        verify(vehicleRepository, never()).findById(any());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicleStatus_ShouldReturnUpdatedVehicleDto() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1, Vehicle.VehicleStatus.IN_USE);

        assertThat(result).isEqualTo(vehicleDto);
        assertThat(vehicle.getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_USE);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void validateTechnicalInspectionDate_WhenDateValid_ShouldNotThrowException() {
        vehicle.setTechnicalInspectionDate(LocalDate.now().plusMonths(6));
        vehicleService.validateTechnicalInspectionDate(vehicle);
    }

    @Test
    void validateTechnicalInspectionDate_WhenDateExpired_ShouldThrowException() {
        vehicle.setTechnicalInspectionDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> vehicleService.validateTechnicalInspectionDate(vehicle))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("expired");
    }
    @Test
    void assignDriverToVehicle_WhenDriverHasMaxVehicles_ShouldThrowException() {
        Vehicle vehicle1 = Vehicle.builder().id(1).build();
        Vehicle vehicle2 = Vehicle.builder().id(2).build();
        List<Vehicle> existingVehicles = new ArrayList<>();
        existingVehicles.add(vehicle1);
        existingVehicles.add(vehicle2);

        driver.setVehicles(existingVehicles);

        Vehicle newVehicle = Vehicle.builder().id(3).build();

        when(vehicleRepository.findById(3)).thenReturn(Optional.of(newVehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignDriverToVehicle(3, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Driver cannot have more than 2 vehicles assigned");

        verify(vehicleRepository, never()).save(any());
    }
    @Test
    void getVehiclesWithUpcomingInspection_ShouldReturnVehiclesWithInspectionInDateRange() {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        List<Vehicle> expectedVehicles = List.of(
                Vehicle.builder()
                        .id(1)
                        .licensePlate("ABC123")
                        .technicalInspectionDate(today.plusDays(10))
                        .build(),
                Vehicle.builder()
                        .id(2)
                        .licensePlate("XYZ789")
                        .technicalInspectionDate(today.plusDays(25))
                        .build()
        );

        when(vehicleRepository.findByTechnicalInspectionDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expectedVehicles);

        List<Vehicle> result = vehicleService.getVehiclesWithUpcomingInspection(30);


        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedVehicles);
        verify(vehicleRepository).findByTechnicalInspectionDateBetween(
                argThat(date -> date.equals(today) || date.isBefore(today.plusDays(1))),
                argThat(date -> date.equals(endDate) || date.isAfter(endDate.minusDays(1)))
        );
    }

    @Test
    void getVehiclesWithUpcomingInspection_WhenNoVehiclesFound_ShouldReturnEmptyList() {

        when(vehicleRepository.findByTechnicalInspectionDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<Vehicle> result = vehicleService.getVehiclesWithUpcomingInspection(30);

        assertThat(result).isEmpty();
        verify(vehicleRepository).findByTechnicalInspectionDateBetween(any(LocalDate.class), any(LocalDate.class));
    }

}