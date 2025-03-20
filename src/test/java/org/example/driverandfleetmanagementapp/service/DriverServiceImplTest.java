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
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {
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
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new java.util.ArrayList<>())
                .build();

        driverDto = DriverDto.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAllDrivers_ReturnsEmptyPage_WhenNoDrivers() {
        Page<Driver> emptyPage = new PageImpl<>(Collections.emptyList());
        when(driverRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        Page<DriverDto> result = driverService.getAllDrivers(0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllDrivers_ReturnsPageOfDrivers_WhenDriversExist() {
        Page<Driver> driverPage = new PageImpl<>(List.of(driver));
        when(driverRepository.findAll(any(PageRequest.class))).thenReturn(driverPage);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        Page<DriverDto> result = driverService.getAllDrivers(0, 10);

        assertThat(result).hasSize(1).contains(driverDto);
    }

    @Test
    void getDriverById_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverById(1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void getDriverById_NotFound_ThrowsException() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getDriverById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDriverByLicenseNumber_Success() {
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(driver));
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.getDriverByLicenseNumber("123456789");

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void getDriversByStatus_Success() {
        when(driverRepository.findByStatus(Driver.DriverStatus.ACTIVE)).thenReturn(List.of(driver));
        when(driverMapper.toDtoList(List.of(driver))).thenReturn(List.of(driverDto));

        List<DriverDto> result = driverService.getDriversByStatus(Driver.DriverStatus.ACTIVE);

        assertThat(result).containsExactly(driverDto);
    }

    @Test
    void getDriversByName_Success() {
        when(driverRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(driver));
        when(driverMapper.toDtoList(List.of(driver))).thenReturn(List.of(driverDto));

        List<DriverDto> result = driverService.getDriversByName("John", "Doe");

        assertThat(result).containsExactly(driverDto);
    }

    @Test
    void getDriversByLicenseType_Success() {
        when(driverRepository.findByLicenseType(Driver.LicenseType.B)).thenReturn(List.of(driver));
        when(driverMapper.toDtoList(List.of(driver))).thenReturn(List.of(driverDto));

        List<DriverDto> result = driverService.getDriversByLicenseType(Driver.LicenseType.B);

        assertThat(result).containsExactly(driverDto);
    }

    @Test
    void createDriver_Success() {
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.empty());
        when(driverMapper.toEntity(driverDto)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.createDriver(driverDto);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void createDriver_DuplicateLicense_ThrowsException() {
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.createDriver(driverDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void updateDriver_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(driver));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.updateDriver(1, driverDto);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void updateDriver_LicenseConflict_ThrowsException() {
        Driver anotherDriver = Driver.builder().id(2).licenseNumber("123456789").build();
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.findByLicenseNumber("123456789")).thenReturn(Optional.of(anotherDriver));

        assertThatThrownBy(() -> driverService.updateDriver(1, driverDto))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void deleteDriver_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findByDriverId(1)).thenReturn(Collections.emptyList());

        driverService.deleteDriver(1);

        verify(driverRepository).delete(driver);
    }

    @Test
    void deleteDriver_WithVehicles_ThrowsException() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findByDriverId(1)).thenReturn(List.of(vehicle));

        assertThatThrownBy(() -> driverService.deleteDriver(1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_VehicleAssigned_ThrowsException() {
        vehicle.setDriver(new Driver());
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    void assignVehicleToDriver_SuspendedDriver_ThrowsException() {
        driver.setStatus(Driver.DriverStatus.SUSPENDED);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_OutOfOrderVehicle_ThrowsException() {
        vehicle.setStatus(Vehicle.VehicleStatus.OUT_OF_ORDER);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_InvalidLicenseForTruck_ThrowsException() {
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        driver.setLicenseType(Driver.LicenseType.B);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }
    @Test
    void assignVehicleToDriver_LicenseTypeC_Van_Success() {
        vehicle.setType(Vehicle.VehicleType.VAN);
        driver.setLicenseType(Driver.LicenseType.C);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }
    @Test
    void assignVehicleToDriver_UnknownLicenseType_ThrowsException() {
        driver.setLicenseType(null);
        vehicle.setType(Vehicle.VehicleType.CAR);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("unknown or invalid license type");
    }

    @Test
    void assignVehicleToDriver_LicenseTypeCE_Van_Success() {
        vehicle.setType(Vehicle.VehicleType.VAN);
        driver.setLicenseType(Driver.LicenseType.CE);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_LicenseCForTruck_Success() {
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        driver.setLicenseType(Driver.LicenseType.C);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_LicenseDForBus_Success() {
        vehicle.setType(Vehicle.VehicleType.BUS);
        driver.setLicenseType(Driver.LicenseType.D);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_LicenseCEForTruck_Success() {
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        driver.setLicenseType(Driver.LicenseType.CE);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_LicenseDEForBus_Success() {
        vehicle.setType(Vehicle.VehicleType.BUS);
        driver.setLicenseType(Driver.LicenseType.DE);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }
    @Test
    void assignVehicleToDriver_LicenseTypeB_Car_Success() {
        vehicle.setType(Vehicle.VehicleType.CAR);
        driver.setLicenseType(Driver.LicenseType.B);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.assignVehicleToDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeB_Bus_ThrowsException() {
        vehicle.setType(Vehicle.VehicleType.BUS);
        driver.setLicenseType(Driver.LicenseType.B);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeB_Truck_ThrowsException() {
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        driver.setLicenseType(Driver.LicenseType.B);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeC_Bus_ThrowsException() {
        vehicle.setType(Vehicle.VehicleType.BUS);
        driver.setLicenseType(Driver.LicenseType.C);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void assignVehicleToDriver_LicenseTypeD_Truck_ThrowsException() {
        vehicle.setType(Vehicle.VehicleType.TRUCK);
        driver.setLicenseType(Driver.LicenseType.D);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.assignVehicleToDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    void removeVehicleFromDriver_Success() {
        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDto(driver)).thenReturn(driverDto);

        DriverDto result = driverService.removeVehicleFromDriver(1, 1);

        assertThat(result).isEqualTo(driverDto);
    }

    @Test
    void removeVehicleFromDriver_NotAssigned_ThrowsException() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> driverService.removeVehicleFromDriver(1, 1))
                .isInstanceOf(BusinessLogicException.class);
    }
}


