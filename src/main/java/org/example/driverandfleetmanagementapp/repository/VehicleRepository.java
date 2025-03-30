package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    //Solving N+1 problem with @EntityGraph

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByType(Vehicle.VehicleType type, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByStatus(Vehicle.VehicleStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Page<Vehicle> findByBrandAndModel(String brand, String model, Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    @EntityGraph(attributePaths = {"driver"})
    List<Vehicle> findByDriverId(Integer driverId);

    boolean existsByDriverId(Integer driverId);


    @EntityGraph(attributePaths = {"driver"})
    @NonNull
    @Override
    Page<Vehicle> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"driver"})
    @NonNull
    @Override
    Optional<Vehicle> findById(@NonNull Integer id);

    List<Vehicle> findByTechnicalInspectionDateBetween(LocalDate startDate, LocalDate endDate);


}

