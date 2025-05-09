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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle.VehicleBuilder defaultVehicleBuilder;

    private Vehicle testVehicle;

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

        testVehicle = defaultVehicleBuilder.build();
        vehicleRepository.save(testVehicle);
    }


    @Test
    void findByLicensePlate_ShouldReturnVehicle(){

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByLicensePlate(testVehicle.getLicensePlate());

        assertThat(vehicleOptional).isPresent();
        assertThat(vehicleOptional.get().getLicensePlate()).isEqualTo("ABC52345");
        assertThat(vehicleOptional.get()).isEqualTo(testVehicle);
    }

    @Test
    void findById_ShouldReturnVehicleId() {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(testVehicle.getId());

        assertThat(vehicleOptional).isPresent();
        assertThat(vehicleOptional.get().getId()).isEqualTo(testVehicle.getId());
    }

    @Test
    void findByStatus_ShouldReturnVehiclesWithStatus() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<Vehicle> vehicles = vehicleRepository.findByStatus(testVehicle.getStatus(), pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent()).contains(testVehicle);
        assertThat(vehicles.getContent().getFirst().getStatus()).isEqualTo(Vehicle.VehicleStatus.AVAILABLE);
    }

    @Test
    void findByBrandAndModel_ShouldReturnVehicles() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehicles = vehicleRepository.findByBrandAndModel( testVehicle.getBrand(), testVehicle.getModel(), pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent()).contains(testVehicle);
        assertThat(vehicles.getContent().getFirst().getBrand()).isEqualTo("Toyota");
        assertThat(vehicles.getContent().getFirst().getModel()).isEqualTo("Corolla");
    }

    @Test
    void findByType_ShouldReturnVehicles() {
        Pageable pageable = PageRequest.of(0, 30);

        Page<Vehicle> vehicles = vehicleRepository.findByType(testVehicle.getType(), pageable);

        assertThat(vehicles.getContent()).isNotEmpty();
        assertThat(vehicles.getContent()).contains(testVehicle);
        assertThat(vehicles.getContent().getFirst().getType()).isEqualTo(Vehicle.VehicleType.CAR);
    }

    @Test
    void findByDriverId_ShouldReturnVehicles() {
        Long driverId = 1L;
        Set<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);

        assertThat(vehicles).isNotEmpty();
        vehicles.forEach(vehicle ->
                assertThat(vehicle.getDriver().getId()).isEqualTo(1)
        );
    }

    @Test
    void findByTechnicalInspectionDateBetween_ShouldReturnVehicles() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(6);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Vehicle> vehicles = vehicleRepository.findByTechnicalInspectionDateBetween(startDate, endDate, pageable);

        assertThat(vehicles).isNotEmpty();
        vehicles.forEach(vehicle -> {
            LocalDate inspectionDate = vehicle.getTechnicalInspectionDate();
            assertThat(inspectionDate).isAfterOrEqualTo(startDate);
            assertThat(inspectionDate).isBeforeOrEqualTo(endDate);
        });
    }


    @Test
    void getAllVehicles_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 30);

        Page<Vehicle> allVehicles = vehicleRepository.findAll(pageable);

        assertThat(allVehicles).isNotNull();
        assertThat(allVehicles.getContent()).isNotEmpty();
        assertThat(allVehicles.getContent()).contains(testVehicle);
        assertThat(allVehicles.getContent().size()).isLessThanOrEqualTo(30);
    }


}

