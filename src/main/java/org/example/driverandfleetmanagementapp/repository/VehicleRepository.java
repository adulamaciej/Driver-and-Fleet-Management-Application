package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);
    List<Vehicle> findByBrandAndModel(String brand, String model);
    List<Vehicle> findByType(Vehicle.VehicleType type);
    List<Vehicle> findByDriverId(Integer driverId);
}

