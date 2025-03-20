package org.example.driverandfleetmanagementapp.repository;


import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void findByLicensePlate_shouldReturnVehicle_whenLicensePlateExists() {

        String existingLicensePlate = "ABC12345";


        Optional<Vehicle> foundVehicle = vehicleRepository.findByLicensePlate(existingLicensePlate);


        assertThat(foundVehicle).isPresent();
        assertThat(foundVehicle.get().getBrand()).isEqualTo("Toyota");
        assertThat(foundVehicle.get().getModel()).isEqualTo("Corolla");
    }

    @Test
    void findByLicensePlate_shouldReturnEmpty_whenLicensePlateDoesNotExist() {

        String nonExistingLicensePlate = "NOT EXISTING";


        Optional<Vehicle> foundVehicle = vehicleRepository.findByLicensePlate(nonExistingLicensePlate);


        assertThat(foundVehicle).isEmpty();
    }

    @Test
    void findByStatus_shouldReturnVehicles_withMatchingStatus() {

        List<Vehicle> inUseVehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.IN_USE);
        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE);
        List<Vehicle> inServiceVehicles = vehicleRepository.findByStatus(Vehicle.VehicleStatus.IN_SERVICE);


        assertThat(inUseVehicles).isNotEmpty();
        assertThat(inUseVehicles).anyMatch(vehicle -> vehicle.getLicensePlate().equals("ABC12345"));

        assertThat(availableVehicles).isNotEmpty();
        assertThat(availableVehicles).anyMatch(vehicle -> vehicle.getLicensePlate().equals("DEF23456"));

        assertThat(inServiceVehicles).isNotEmpty();
        assertThat(inServiceVehicles).anyMatch(vehicle -> vehicle.getLicensePlate().equals("GHI34567"));
    }

    @Test
    void findByBrandAndModel_shouldReturnVehicles_withMatchingBrandAndModel() {

        List<Vehicle> toyotaCorollas = vehicleRepository.findByBrandAndModel("Toyota", "Corolla");
        List<Vehicle> fordFocuses = vehicleRepository.findByBrandAndModel("Ford", "Focus");
        List<Vehicle> nonExistingVehicles = vehicleRepository.findByBrandAndModel("Not existing", "Marka");


        assertThat(toyotaCorollas).hasSize(1);
        assertThat(toyotaCorollas.getFirst().getLicensePlate()).isEqualTo("ABC12345");

        assertThat(fordFocuses).hasSize(1);
        assertThat(fordFocuses.getFirst().getLicensePlate()).isEqualTo("DEF23456");

        assertThat(nonExistingVehicles).isEmpty();
    }

    @Test
    void findByType_shouldReturnVehicles_withMatchingType() {

        List<Vehicle> cars = vehicleRepository.findByType(Vehicle.VehicleType.CAR);
        List<Vehicle> vans = vehicleRepository.findByType(Vehicle.VehicleType.VAN);
        List<Vehicle> trucks = vehicleRepository.findByType(Vehicle.VehicleType.TRUCK);


        assertThat(cars).isNotEmpty();
        assertThat(cars).anyMatch(vehicle -> vehicle.getLicensePlate().equals("ABC12345"));

        assertThat(vans).isNotEmpty();
        assertThat(vans).anyMatch(vehicle -> vehicle.getLicensePlate().equals("STU78901"));

        assertThat(trucks).isNotEmpty();
        assertThat(trucks).anyMatch(vehicle -> vehicle.getLicensePlate().equals("MNO56789"));
    }

    @Test
    void findByDriverId_shouldReturnVehicles_assignedToDriver() {

        Integer driverId = 1;

        List<Vehicle> driverVehicles = vehicleRepository.findByDriverId(driverId);


        assertThat(driverVehicles).hasSize(2);
        assertThat(driverVehicles).extracting(Vehicle::getLicensePlate)
                .containsExactlyInAnyOrder("ABC12345", "DEF23456");
    }

    @Test
    void findByDriverId_shouldReturnEmpty_whenDriverIdDoesNotExist() {

        Integer nonExistingDriverId = 9999;


        List<Vehicle> vehicles = vehicleRepository.findByDriverId(nonExistingDriverId);


        assertThat(vehicles).isEmpty();
    }
}