package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);
    Page<Vehicle> findByStatus(Vehicle.VehicleStatus status, Pageable pageable);
    Page<Vehicle> findByBrandAndModel(String brand, String model, Pageable pageable);
    Page<Vehicle> findByType(Vehicle.VehicleType type, Pageable pageable);
    List<Vehicle> findByDriverId(Integer driverId);
    boolean existsByDriverId(Integer driverId);



}

