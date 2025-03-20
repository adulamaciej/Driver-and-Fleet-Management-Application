package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.dto.VehicleBasicDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private DriverDto driverDto;
    private List<DriverDto> driverDtoList;
    private Page<DriverDto> driverDtoPage;

    @BeforeEach
    void setUp() {
        driverDto = DriverDto.builder()
                .id(1)
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("987654321")
                .email("jan.kowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new ArrayList<>())
                .build();

        DriverDto driverDto2 = DriverDto.builder()
                .id(2)
                .firstName("Anna")
                .lastName("Nowak")
                .licenseNumber("987654321")
                .licenseType(Driver.LicenseType.C)
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .phoneNumber("123456789")
                .email("anna.nowak@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .vehicles(new ArrayList<>())
                .build();

        driverDtoList = Arrays.asList(driverDto, driverDto2);
        driverDtoPage = new PageImpl<>(driverDtoList);
    }

    @Test
    void getAllDrivers_ShouldReturnPageOfDrivers() {

        when(driverService.getAllDrivers(anyInt(), anyInt())).thenReturn(driverDtoPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getAllDrivers(0, 10);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDtoPage);
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).hasSize(2);
        verify(driverService, times(1)).getAllDrivers(0, 10);
    }

    @Test
    void getDriverById_ShouldReturnDriver() {

        when(driverService.getDriverById(anyInt())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverById(1);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService, times(1)).getDriverById(1);
    }

    @Test
    void getDriverByLicenseNumber_ShouldReturnDriver() {

        when(driverService.getDriverByLicenseNumber(anyString())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverByLicenseNumber("123456789");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService, times(1)).getDriverByLicenseNumber("123456789");
    }

    @Test
    void getDriversByStatus_ShouldReturnDriversList() {

        when(driverService.getDriversByStatus(any(Driver.DriverStatus.class))).thenReturn(driverDtoList);

        ResponseEntity<List<DriverDto>> response = driverController.getDriversByStatus(Driver.DriverStatus.ACTIVE);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDtoList);
        assertThat(response.getBody()).hasSize(2);
        verify(driverService, times(1)).getDriversByStatus(Driver.DriverStatus.ACTIVE);
    }

    @Test
    void searchDriversByName_ShouldReturnDriversList() {

        when(driverService.getDriversByName(anyString(), anyString())).thenReturn(Collections.singletonList(driverDto));

        ResponseEntity<List<DriverDto>> response = driverController.searchDriversByName("Jan", "Kowalski");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(driverDto);
        verify(driverService, times(1)).getDriversByName("Jan", "Kowalski");
    }

    @Test
    void getDriversByLicenseType_ShouldReturnDriversList() {

        when(driverService.getDriversByLicenseType(any(Driver.LicenseType.class))).thenReturn(Collections.singletonList(driverDto));

        ResponseEntity<List<DriverDto>> response = driverController.getDriversByLicenseType(Driver.LicenseType.B);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(driverDto);
        verify(driverService, times(1)).getDriversByLicenseType(Driver.LicenseType.B);
    }

    @Test
    void createDriver_ShouldReturnCreatedDriver() {

        when(driverService.createDriver(any(DriverDto.class))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.createDriver(driverDto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService, times(1)).createDriver(driverDto);
    }

    @Test
    void updateDriver_ShouldReturnUpdatedDriver() {

        when(driverService.updateDriver(anyInt(), any(DriverDto.class))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.updateDriver(1, driverDto);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService, times(1)).updateDriver(1, driverDto);
    }

    @Test
    void deleteDriver_ShouldReturnNoContent() {

        doNothing().when(driverService).deleteDriver(anyInt());

        ResponseEntity<Void> response = driverController.deleteDriver(1);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(driverService, times(1)).deleteDriver(1);
    }

    @Test
    void assignVehicleToDriver_ShouldReturnUpdatedDriver() {

        VehicleBasicDto vehicleBasicDto = VehicleBasicDto.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .build();

        DriverDto updatedDriverDto = driverDto.toBuilder()
                .vehicles(Collections.singletonList(vehicleBasicDto))
                .build();

        when(driverService.assignVehicleToDriver(anyInt(), anyInt())).thenReturn(updatedDriverDto);

        ResponseEntity<DriverDto> response = driverController.assignVehicleToDriver(1, 1);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDriverDto);
        assertThat(Objects.requireNonNull(response.getBody()).getVehicles()).hasSize(1);
        verify(driverService, times(1)).assignVehicleToDriver(1, 1);
    }

    @Test
    void removeVehicleFromDriver_ShouldReturnUpdatedDriver() {

        when(driverService.removeVehicleFromDriver(anyInt(), anyInt())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.removeVehicleFromDriver(1, 1);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        assertThat(Objects.requireNonNull(response.getBody()).getVehicles()).isEmpty();
        verify(driverService, times(1)).removeVehicleFromDriver(1, 1);
    }
}