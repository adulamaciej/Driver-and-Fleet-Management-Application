package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle.VehicleBuilder defaultVehicleBuilder;

    @BeforeEach
    void setUp() {
        defaultVehicleBuilder = Vehicle.builder()
                .licensePlate("ABC52345")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 15))
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .mileage(15000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE);
    }


    @Test
    void findByLicensePlate_ShouldReturnVehicle(){
        Vehicle vehicle = defaultVehicleBuilder.build();
        vehicle = vehicleRepository.save(vehicle);

        String savedLicensePlate = vehicle.getLicensePlate();

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByLicensePlate(savedLicensePlate);

        assertThat(vehicleOptional).isPresent();
        assertThat(vehicleOptional.get().getLicensePlate()).isEqualTo("ABC52345");
    }

    @Test
    void findById_ShouldReturnVehicleId() {
        Vehicle vehicle = defaultVehicleBuilder.build();
        vehicleRepository.save(vehicle);

        Integer savedId = vehicle.getId();

        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(savedId);

        assertThat(vehicleOptional).isPresent();
        assertThat(vehicleOptional.get().getId()).isEqualTo(savedId);
    }
    @Test
    void findByStatus_ShouldReturnVehiclesWithStatus() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE, pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent().getFirst().getStatus()).isEqualTo(Vehicle.VehicleStatus.AVAILABLE);
    }
    @Test
    void findByBrandAndModel_ShouldReturnVehicles() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehicles = vehicleRepository.findByBrandAndModel("Toyota", "Corolla", pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent().getFirst().getBrand()).isEqualTo("Toyota");
        assertThat(vehicles.getContent().getFirst().getModel()).isEqualTo("Corolla");
    }

    @Test
    void findByType_ShouldReturnVehicles() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehicles = vehicleRepository.findByType(Vehicle.VehicleType.CAR, pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent().getFirst().getType()).isEqualTo(Vehicle.VehicleType.CAR);
    }

    @Test
    void findByDriverId_ShouldReturnVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findByDriverId(1);

        assertThat(vehicles).isNotEmpty();
        vehicles.forEach(vehicle ->
                assertThat(vehicle.getDriver().getId()).isEqualTo(1)
        );
    }

    @Test
    void findByTechnicalInspectionDateBetween_ShouldReturnVehicles() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(6);

        List<Vehicle> vehicles = vehicleRepository.findByTechnicalInspectionDateBetween(startDate, endDate);

        assertThat(vehicles).isNotEmpty();
        vehicles.forEach(vehicle -> {
            LocalDate inspectionDate = vehicle.getTechnicalInspectionDate();
            assertThat(inspectionDate).isAfterOrEqualTo(startDate);
            assertThat(inspectionDate).isBeforeOrEqualTo(endDate);
        });
    }

    @Test
    void getAllVehicles_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> allVehicles = vehicleRepository.findAll(pageable);

        assertThat(allVehicles).isNotNull();
        assertThat(allVehicles.getContent()).isNotEmpty();
        assertThat(allVehicles.getContent().size()).isLessThanOrEqualTo(10);
    }
}

