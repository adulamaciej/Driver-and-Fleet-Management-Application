package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.ResourceConflictException;
import org.example.driverandfleetmanagementapp.mapper.VehicleMapper;
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
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTest {
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
        driver = Driver.builder()
                .id(1)
                .licenseType(Driver.LicenseType.B)
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.now().minusYears(1))
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .mileage(50000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        vehicleDto = VehicleDto.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.now().minusYears(1))
                .technicalInspectionDate(LocalDate.now().plusMonths(6))
                .mileage(50000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAllVehicles_ReturnsEmptyPage() {
        Page<Vehicle> emptyPage = new PageImpl<>(Collections.emptyList());
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Page<VehicleDto> result = vehicleService.getAllVehicles(0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllVehicles_ReturnsVehicles() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(vehiclePage);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        Page<VehicleDto> result = vehicleService.getAllVehicles(0, 10);

        assertThat(result).hasSize(1).contains(vehicleDto);
    }

    @Test
    void getVehicleById_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleById(1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehicleByLicensePlate_Success() {
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getVehicleByLicensePlate("ABC123");

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void getVehiclesByStatus_Success() {
        when(vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtoList(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);

        assertThat(result).containsExactly(vehicleDto);
    }

    @Test
    void getVehiclesByBrandAndModel_Success() {
        when(vehicleRepository.findByBrandAndModel("Toyota", "Corolla")).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtoList(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByBrandAndModel("Toyota", "Corolla");

        assertThat(result).containsExactly(vehicleDto);
    }

    @Test
    void getVehiclesByType_Success() {
        when(vehicleRepository.findByType(Vehicle.VehicleType.CAR)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtoList(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByType(Vehicle.VehicleType.CAR);

        assertThat(result).containsExactly(vehicleDto);
    }

    @Test
    void getVehiclesByDriverId_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findByDriverId(1)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtoList(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getVehiclesByDriverId(1);

        assertThat(result).containsExactly(vehicleDto);
    }

    @Test
    void createVehicle_Success() {
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.empty());
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void createVehicle_DuplicateLicensePlate_ThrowsException() {
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.createVehicle(vehicleDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void updateVehicle_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicle(1, vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void updateVehicle_LicensePlateConflict_ThrowsException() {
        Vehicle anotherVehicle = Vehicle.builder().id(2).licensePlate("ABC123").build();
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(anotherVehicle));

        assertThatThrownBy(() -> vehicleService.updateVehicle(1, vehicleDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void updateVehicle_NullLicensePlate_Success() {
        vehicleDto.setLicensePlate(null);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByLicensePlate(null)).thenReturn(Optional.empty());
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicle(1, vehicleDto);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void deleteVehicle_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_WithDriverNotInUse_Success() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);

        vehicleService.deleteVehicle(1);

        verify(vehicleRepository).delete(vehicle);
        verify(driverRepository).save(driver);
    }

    @Test
    void deleteVehicle_InUse_ThrowsException() {
        vehicle.setDriver(driver);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.deleteVehicle(1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_AlreadyAssigned_ThrowsException() {
        vehicle.setDriver(driver);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void assignVehicleToDriver_OutOfOrder_ThrowsException() {
        vehicle.setStatus(Vehicle.VehicleStatus.OUT_OF_ORDER);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        lenient().when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("Cannot assign vehicle with status OUT_OF_ORDER to a driver");
    }
    @Test
    void assignVehicleToDriver_LicenseTypeC_Van_Success() {
        driver.setLicenseType(Driver.LicenseType.C);
        vehicle.setType(Vehicle.VehicleType.VAN);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }
    @Test
    void assignVehicleToDriver_UnknownLicenseType_ThrowsException() {
        driver.setLicenseType(null);
        vehicle.setType(Vehicle.VehicleType.CAR);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeCE_Van_Success() {
        driver.setLicenseType(Driver.LicenseType.CE);
        vehicle.setType(Vehicle.VehicleType.VAN);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_SuspendedDriver_ThrowsException() {
        driver.setStatus(Driver.DriverStatus.SUSPENDED);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_InvalidLicenseType_ThrowsException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeC_Truck_Success() {
        driver.setLicenseType(Driver.LicenseType.C);
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeD_Bus_Success() {
        driver.setLicenseType(Driver.LicenseType.D);
        vehicle.setType(Vehicle.VehicleType.BUS);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeCE_Truck_Success() {
        driver.setLicenseType(Driver.LicenseType.CE);
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeDE_Bus_Success() {
        driver.setLicenseType(Driver.LicenseType.DE);
        vehicle.setType(Vehicle.VehicleType.BUS);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }
    @Test
    void assignVehicleToDriver_LicenseTypeB_Car_Success() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.CAR);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeB_Truck_ThrowsException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        lenient().when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeB_Bus_ThrowsException() {
        driver.setLicenseType(Driver.LicenseType.B);
        vehicle.setType(Vehicle.VehicleType.BUS);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        lenient().when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeC_Bus_ThrowsException() {
        driver.setLicenseType(Driver.LicenseType.C);
        vehicle.setType(Vehicle.VehicleType.BUS);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        lenient().when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeD_Truck_ThrowsException() {
        driver.setLicenseType(Driver.LicenseType.D);
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        lenient().when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> vehicleService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }



    @Test
    void removeDriverFromVehicle_Success() {
        vehicle.setDriver(driver);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.removeDriverFromVehicle(1);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void removeDriverFromVehicle_NoDriver_ThrowsException() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.removeDriverFromVehicle(1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void updateVehicleMileage_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleMileage(1, 60000.0);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void updateVehicleMileage_Negative_ThrowsException() {
        lenient().when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> vehicleService.updateVehicleMileage(1, -1.0))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void updateVehicleMileage_LowerThanCurrent_Success() {
        vehicle.setMileage(60000.0);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleMileage(1, 50000.0);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void updateVehicleStatus_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1, Vehicle.VehicleStatus.IN_USE);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void updateVehicleStatus_InUseWithNoDriver_Success() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1, Vehicle.VehicleStatus.IN_USE);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void updateVehicleStatus_OutOfOrderWithDriver_Success() {
        vehicle.setDriver(driver);
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicleStatus(1, Vehicle.VehicleStatus.OUT_OF_ORDER);

        assertThat(result).isEqualTo(vehicleDto);
    }

    @Test
    void validateTechnicalInspectionDate_Valid() {
        vehicleService.validateTechnicalInspectionDate(vehicle);
    }

    @Test
    void validateTechnicalInspectionDate_Expired_ThrowsException() {
        vehicle.setTechnicalInspectionDate(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> vehicleService.validateTechnicalInspectionDate(vehicle))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void validateTechnicalInspectionDate_ExpiringSoon_DoesNotThrow() {
        vehicle.setTechnicalInspectionDate(LocalDate.now().plusDays(15));
        vehicleService.validateTechnicalInspectionDate(vehicle);
    }
}
