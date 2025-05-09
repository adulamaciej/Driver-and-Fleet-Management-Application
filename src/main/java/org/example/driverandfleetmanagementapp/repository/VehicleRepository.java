package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    //Solving N+1 problem with @EntityGraph

    @EntityGraph(attributePaths = {"driver"})
    @NonNull
    @Override
    Optional<Vehicle> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"driver"})
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByType(Vehicle.VehicleType type, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByStatus(Vehicle.VehicleStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByBrandAndModel(String brand, String model, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Set<Vehicle> findByDriverId(Long driverId); // getVehiclesByDriverId, max is 2, set for uniqueness

    boolean existsByDriverId(Long driverId); // efficiency -  only returns boolean

    @EntityGraph(attributePaths = {"driver"})
    @NonNull
    @Override
    Page<Vehicle> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByTechnicalInspectionDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);


}

