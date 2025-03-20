package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.DriverBasicDto;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.VehicleService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private VehicleDto vehicleDto;
    private List<VehicleDto> vehicleDtoList;
    private Page<VehicleDto> vehicleDtoPage;

    @BeforeEach
    void setUp() {
        vehicleDto = VehicleDto.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 15))
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .driver(null)
                .build();

        VehicleDto vehicleDto2 = VehicleDto.builder()
                .id(2)
                .licensePlate("DEF456")
                .brand("Ford")
                .model("Focus")
                .productionYear(2019)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2019, 5, 10))
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .mileage(15000.0)
                .status(Vehicle.VehicleStatus.IN_USE)
                .driver(DriverBasicDto.builder().id(1).firstName("Jan").lastName("Kowalski").licenseNumber("123456789").build())
                .build();

        vehicleDtoList = Arrays.asList(vehicleDto, vehicleDto2);
        vehicleDtoPage = new PageImpl<>(vehicleDtoList);
    }

    @Test
    void getAllVehicles_ShouldReturnPageOfVehicles() {

        when(vehicleService.getAllVehicles(anyInt(), anyInt())).thenReturn(vehicleDtoPage);


        ResponseEntity<Page<VehicleDto>> response = vehicleController.getAllVehicles(0, 10);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDtoPage);
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).hasSize(2);
        verify(vehicleService, times(1)).getAllVehicles(0, 10);
    }

    @Test
    void getVehicleById_ShouldReturnVehicle() {

        when(vehicleService.getVehicleById(anyInt())).thenReturn(vehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.getVehicleById(1);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehicleById(1);
    }

    @Test
    void getVehicleByLicensePlate_ShouldReturnVehicle() {

        when(vehicleService.getVehicleByLicensePlate(anyString())).thenReturn(vehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.getVehicleByLicensePlate("ABC123");


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehicleByLicensePlate("ABC123");
    }

    @Test
    void getVehiclesByStatus_ShouldReturnVehiclesList() {

        when(vehicleService.getVehiclesByStatus(any(Vehicle.VehicleStatus.class))).thenReturn(Collections.singletonList(vehicleDto));


        ResponseEntity<List<VehicleDto>> response = vehicleController.getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);
    }

    @Test
    void searchVehiclesByBrandAndModel_ShouldReturnVehiclesList() {

        when(vehicleService.getVehiclesByBrandAndModel(anyString(), anyString())).thenReturn(Collections.singletonList(vehicleDto));


        ResponseEntity<List<VehicleDto>> response = vehicleController.searchVehiclesByBrandAndModel("Toyota", "Corolla");


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehiclesByBrandAndModel("Toyota", "Corolla");
    }

    @Test
    void getVehiclesByType_ShouldReturnVehiclesList() {

        when(vehicleService.getVehiclesByType(any(Vehicle.VehicleType.class))).thenReturn(Collections.singletonList(vehicleDto));


        ResponseEntity<List<VehicleDto>> response = vehicleController.getVehiclesByType(Vehicle.VehicleType.CAR);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehiclesByType(Vehicle.VehicleType.CAR);
    }

    @Test
    void getVehiclesByDriverId_ShouldReturnVehiclesList() {

        when(vehicleService.getVehiclesByDriverId(anyInt())).thenReturn(Collections.singletonList(vehicleDto));


        ResponseEntity<List<VehicleDto>> response = vehicleController.getVehiclesByDriverId(1);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).getVehiclesByDriverId(1);
    }

    @Test
    void createVehicle_ShouldReturnCreatedVehicle() {

        when(vehicleService.createVehicle(any(VehicleDto.class))).thenReturn(vehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.createVehicle(vehicleDto);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).createVehicle(vehicleDto);
    }

    @Test
    void updateVehicle_ShouldReturnUpdatedVehicle() {

        when(vehicleService.updateVehicle(anyInt(), any(VehicleDto.class))).thenReturn(vehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.updateVehicle(1, vehicleDto);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        verify(vehicleService, times(1)).updateVehicle(1, vehicleDto);
    }

    @Test
    void deleteVehicle_ShouldReturnNoContent() {

        doNothing().when(vehicleService).deleteVehicle(anyInt());


        ResponseEntity<Void> response = vehicleController.deleteVehicle(1);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(vehicleService, times(1)).deleteVehicle(1);
    }

    @Test
    void assignVehicleToDriver_ShouldReturnUpdatedVehicle() {

        VehicleDto updatedVehicleDto = vehicleDto.toBuilder()
                .status(Vehicle.VehicleStatus.IN_USE)
                .driver(DriverBasicDto.builder().id(1).firstName("Jan").lastName("Kowalski").licenseNumber("123456789").build())
                .build();

        when(vehicleService.assignVehicleToDriver(anyInt(), anyInt())).thenReturn(updatedVehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.assignVehicleToDriver(1, 1);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedVehicleDto);
        assertThat(Objects.requireNonNull(response.getBody()).getDriver()).isNotNull();
        verify(vehicleService, times(1)).assignVehicleToDriver(1, 1);
    }

    @Test
    void removeDriverFromVehicle_ShouldReturnUpdatedVehicle() {

        when(vehicleService.removeDriverFromVehicle(anyInt())).thenReturn(vehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.removeDriverFromVehicle(1);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vehicleDto);
        assertThat(Objects.requireNonNull(response.getBody()).getDriver()).isNull();
        verify(vehicleService, times(1)).removeDriverFromVehicle(1);
    }

    @Test
    void updateVehicleMileage_ShouldReturnUpdatedVehicle() {

        VehicleDto updatedVehicleDto = vehicleDto.toBuilder()
                .mileage(12000.0)
                .build();

        when(vehicleService.updateVehicleMileage(anyInt(), anyDouble())).thenReturn(updatedVehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.updateVehicleMileage(1, 12000.0);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedVehicleDto);
        assertThat(Objects.requireNonNull(response.getBody()).getMileage()).isEqualTo(12000.0);
        verify(vehicleService, times(1)).updateVehicleMileage(1, 12000.0);
    }

    @Test
    void updateVehicleStatus_ShouldReturnUpdatedVehicle() {

        VehicleDto updatedVehicleDto = vehicleDto.toBuilder()
                .status(Vehicle.VehicleStatus.IN_SERVICE)
                .build();

        when(vehicleService.updateVehicleStatus(anyInt(), any(Vehicle.VehicleStatus.class))).thenReturn(updatedVehicleDto);


        ResponseEntity<VehicleDto> response = vehicleController.updateVehicleStatus(1, Vehicle.VehicleStatus.IN_SERVICE);


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedVehicleDto);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(Vehicle.VehicleStatus.IN_SERVICE);
        verify(vehicleService, times(1)).updateVehicleStatus(1, Vehicle.VehicleStatus.IN_SERVICE);
    }
}
