package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.service.driver.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
                .id(1L)
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
        when(driverService.getAllDrivers(any(Pageable.class))).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getAllDrivers(0, 10,"id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getAllDrivers(any(Pageable.class));
    }

    @Test
    void getDriverById_ShouldReturnDriver() {
        when(driverService.getDriverById(1L)).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).getDriverById(1L);
    }

    @Test
    void getDriverByLicenseNumber_ShouldReturnDriver() {
        when(driverService.getDriverByLicenseNumber("123456789")).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.getDriverByLicenseNumber("123456789");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).getDriverByLicenseNumber("123456789");
    }

    @Test
    void getDriversByStatus_ShouldReturnDrivers() {
        when(driverService.getDriversByStatus(eq(Driver.DriverStatus.ACTIVE),  any(Pageable.class))).thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getDriversByStatus(Driver.DriverStatus.ACTIVE, 0, 10,"id",Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getDriversByStatus(eq(Driver.DriverStatus.ACTIVE), any(Pageable.class));
    }

    @Test
    void searchDriversByName_ShouldReturnDrivers() {
        when(driverService.getDriversByFirstAndLastName(eq("John"), eq("Doe"), any(Pageable.class)))
                .thenReturn(driverPage);

        ResponseEntity<Page<DriverDto>> response = driverController.getDriversByFirstAndLastName(
                "John", "Doe", 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getBody()).isEqualTo(driverPage);

        verify(driverService).getDriversByFirstAndLastName(
                eq("John"),
                eq("Doe"),
                eq(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")))
        );
    }

    @Test
    void getDriversByLicenseType_ShouldReturnDrivers() {
        when(driverService.getDriversByLicenseType(eq(Driver.LicenseType.B), any(Pageable.class))).thenReturn(driverPage);
        ResponseEntity<Page<DriverDto>> response = driverController.getDriversByLicenseType(Driver.LicenseType.B, 0, 10,"id",Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverPage);
        verify(driverService).getDriversByLicenseType(eq(Driver.LicenseType.B), any(Pageable.class));
    }

    @Test
    void updateDriverStatus_ShouldReturnUpdatedDriver(){

        DriverDto updatedDriverDto = driverDto.toBuilder()
                .status(Driver.DriverStatus.SUSPENDED)
                .build();

        when(driverService.updateDriverStatus(1L, Driver.DriverStatus.SUSPENDED)).thenReturn(updatedDriverDto);

        ResponseEntity<DriverDto> response = driverController.updateDriverStatus(
                1L, Driver.DriverStatus.SUSPENDED);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDriverDto);

        verify(driverService).updateDriverStatus(1L, Driver.DriverStatus.SUSPENDED);
    }


    @Test
    void createDriver_ShouldReturnCreatedDriver() {
        when(driverService.createDriver(eq(driverDto))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.createDriver(driverDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).createDriver(eq(driverDto));
    }

    @Test
    void updateDriver_ShouldReturnUpdatedDriver() {
        when(driverService.updateDriver(eq(1L), eq(driverDto))).thenReturn(driverDto);

        ResponseEntity<DriverDto> response = driverController.updateDriver(1L, driverDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(driverDto);
        verify(driverService).updateDriver(eq(1L), eq(driverDto));
    }

    @Test
    void deleteDriver_ShouldReturnNoContent() {
        doNothing().when(driverService).deleteDriver(1L);

        ResponseEntity<Void> response = driverController.deleteDriver(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(driverService).deleteDriver(1L);
    }


}