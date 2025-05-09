package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Optional;


public interface DriverRepository extends JpaRepository<Driver, Long> {


    //Solving N+1 problem with @EntityGraph

    @EntityGraph(attributePaths = {"vehicles"})
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    @EntityGraph(attributePaths = {"vehicles"})
    Page<Driver> findByStatus(Driver.DriverStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"vehicles"})
    List<Driver> findByFirstNameAndLastName(String firstName, String lastName);

    @EntityGraph(attributePaths = {"vehicles"})
    Page<Driver> findByLicenseType(Driver.LicenseType licenseType, Pageable pageable);

    @EntityGraph(attributePaths = {"vehicles"})
    @NonNull
    @Override
    Page<Driver> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"vehicles"})
    @NonNull
    @Override
    Optional<Driver> findById(@NonNull Long id);

}
