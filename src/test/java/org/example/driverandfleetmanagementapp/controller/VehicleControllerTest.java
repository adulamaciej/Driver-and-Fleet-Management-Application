package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.DriverBasicDto;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.vehicle.VehicleService;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private VehicleDto vehicleDto;
    private Page<VehicleDto> vehiclePage;
    private List<VehicleDto> vehicleList;

    @BeforeEach
    void setUp() {
        DriverBasicDto driverBasicDto = DriverBasicDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .licenseNumber("123456789")
                .build();

        vehicleDto = VehicleDto.builder()
                .id(1L)
                .licensePlate("ABC12345")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 5, 15))
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .mileage(15000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .driver(driverBasicDto)
                .build();

        vehicleList = List.of(vehicleDto);
        vehiclePage = new PageImpl<>(vehicleList);
    }

    @Test
    void getAllVehicles_ShouldReturnPageOfVehicles() {
        when(vehicleService.getAllVehicles(any(Pageable.class))).thenReturn(vehiclePage);

        ResponseEntity<Page<VehicleDto>> response = vehicleController.getAllVehicles(0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehiclePage);
        verify(vehicleService).getAllVehicles(any(Pageable.class));
    }

    @Test
    void getVehicleById_ShouldReturnVehicle() {
        when(vehicleService.getVehicleById(1L)).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.getVehicleById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).getVehicleById(1L);
    }

    @Test
    void getVehicleByLicensePlate_ShouldReturnVehicle() {
        when(vehicleService.getVehicleByLicensePlate("ABC12345")).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.getVehicleByLicensePlate("ABC12345");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).getVehicleByLicensePlate("ABC12345");
    }

    @Test
    void getVehiclesByStatus_ShouldReturnVehicles() {
        when(vehicleService.getVehiclesByStatus(eq(Vehicle.VehicleStatus.AVAILABLE), any(Pageable.class))).thenReturn(vehiclePage);

        ResponseEntity<Page<VehicleDto>> response = vehicleController.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehiclePage);
        verify(vehicleService).getVehiclesByStatus(eq(Vehicle.VehicleStatus.AVAILABLE), any(Pageable.class));

    }

    @Test
    void searchVehiclesByBrandAndModel_ShouldReturnVehicles() {
        when(vehicleService.getVehiclesByBrandAndModel(eq("Toyota"), eq("Corolla"), any(Pageable.class))).thenReturn(vehiclePage);


        ResponseEntity<Page<VehicleDto>> response = vehicleController.searchVehiclesByBrandAndModel(
                "Toyota", "Corolla", 0, 10, "id", Sort.Direction.ASC
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehiclePage);
        verify(vehicleService).getVehiclesByBrandAndModel(eq("Toyota"), eq("Corolla"), any(Pageable.class));
    }

    @Test
    void getVehiclesByType_ShouldReturnVehicles() {
        PageRequest expectedPageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(vehicleService.getVehiclesByType(eq(Vehicle.VehicleType.CAR), eq(expectedPageRequest)))
                .thenReturn(vehiclePage);

        ResponseEntity<Page<VehicleDto>> response = vehicleController.getVehiclesByType(
                Vehicle.VehicleType.CAR, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehiclePage);

        verify(vehicleService).getVehiclesByType(eq(Vehicle.VehicleType.CAR), eq(expectedPageRequest));
    }


    @Test
    void getVehiclesByDriverId_ShouldReturnVehicles() {
        Set<VehicleDto> vehicleSet = Set.copyOf(vehicleList);
        when(vehicleService.getVehiclesByDriverId(1L)).thenReturn(vehicleSet);

        ResponseEntity<Set<VehicleDto>> response = vehicleController.getVehiclesByDriverId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleSet);
        verify(vehicleService).getVehiclesByDriverId(1L);
    }


    @Test
    void createVehicle_ShouldReturnCreatedVehicle() {
        when(vehicleService.createVehicle(vehicleDto)).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.createVehicle(vehicleDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).createVehicle(vehicleDto);
    }

    @Test
    void updateVehicle_ShouldReturnUpdatedVehicle() {
        when(vehicleService.updateVehicle(1L, vehicleDto)).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.updateVehicle(1L, vehicleDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).updateVehicle(1L, vehicleDto);
    }

    @Test
    void deleteVehicle_ShouldReturnNoContent() {
        doNothing().when(vehicleService).deleteVehicle(1L);
        ResponseEntity<Void> response = vehicleController.deleteVehicle(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(vehicleService).deleteVehicle(1L);
    }


    @Test
    void updateVehicleMileage_ShouldReturnUpdatedVehicle() {
        when(vehicleService.updateVehicleMileage(1L, 2000.0)).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.updateVehicleMileage(1L, 2000.0);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).updateVehicleMileage(1L, 2000.0);
    }

    @Test
    void updateVehicleStatus_ShouldReturnUpdatedVehicle() {
        when(vehicleService.updateVehicleStatus(1L, Vehicle.VehicleStatus.IN_SERVICE)).thenReturn(vehicleDto);

        ResponseEntity<VehicleDto> response = vehicleController.updateVehicleStatus(1L, Vehicle.VehicleStatus.IN_SERVICE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService).updateVehicleStatus(1L, Vehicle.VehicleStatus.IN_SERVICE);
    }

}