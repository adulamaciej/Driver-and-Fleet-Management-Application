package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
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
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DriverControllerTest {

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    private DriverDto driverDto;
    private Page<DriverDto> driverPage;

    @BeforeEach
    void setUp() {
        driverDto = DriverDto.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1985, 1, 15))
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .status(Driver.DriverStatus.ACTIVE)
                .build();

        driverPage = new PageImpl<>(List.of(driverDto));
    }

    @Test
    void getAllDrivers_ShouldReturnPageOfDrivers() {
        when(driverService.getAllDrivers(anyInt(), anyInt())).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getAllDrivers(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getAllDrivers(0, 10);
    }

    @Test
    void getDriverById_ShouldReturnDriver() {
        when(driverService.getDriverById(anyInt())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).getDriverById(1);
    }

    @Test
    void getDriverByLicenseNumber_ShouldReturnDriver() {
        when(driverService.getDriverByLicenseNumber(anyString())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverByLicenseNumber("123456789");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).getDriverByLicenseNumber("123456789");
    }

    @Test
    void getDriversByStatus_ShouldReturnDrivers() {
        when(driverService.getDriversByStatus(any(Driver.DriverStatus.class), anyInt(), anyInt())).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getDriversByStatus(Driver.DriverStatus.ACTIVE, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getDriversByStatus(Driver.DriverStatus.ACTIVE, 0, 10);
    }

    @Test
    void searchDriversByName_ShouldReturnDrivers() {
        when(driverService.getDriversByName(anyString(), anyString(), anyInt(), anyInt())).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.searchDriversByName("John", "Doe", 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getDriversByName("John", "Doe", 0, 10);
    }

    @Test
    void getDriversByLicenseType_ShouldReturnDrivers() {
        when(driverService.getDriversByLicenseType(any(Driver.LicenseType.class), anyInt(), anyInt())).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getDriversByLicenseType(Driver.LicenseType.B, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getDriversByLicenseType(Driver.LicenseType.B, 0, 10);
    }

    @Test
    void createDriver_ShouldReturnCreatedDriver() {
        when(driverService.createDriver(any(DriverDto.class))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.createDriver(driverDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).createDriver(driverDto);
    }

    @Test
    void updateDriver_ShouldReturnUpdatedDriver() {
        when(driverService.updateDriver(anyInt(), any(DriverDto.class))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.updateDriver(1, driverDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).updateDriver(1, driverDto);
    }

    @Test
    void deleteDriver_ShouldReturnNoContent() {
        doNothing().when(driverService).deleteDriver(anyInt());

        ResponseEntity<Void> response = driverController.deleteDriver(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(driverService).deleteDriver(1);
    }

    @Test
    void assignVehicleToDriver_ShouldReturnUpdatedDriver() {
        when(driverService.assignVehicleToDriver(anyInt(), anyInt())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.assignVehicleToDriver(1, 2);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).assignVehicleToDriver(1, 2);
    }

    @Test
    void removeVehicleFromDriver_ShouldReturnUpdatedDriver() {
        when(driverService.removeVehicleFromDriver(anyInt(), anyInt())).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.removeVehicleFromDriver(1, 2);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).removeVehicleFromDriver(1, 2);
    }
}