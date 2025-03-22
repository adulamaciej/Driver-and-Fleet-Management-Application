package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Page<Driver> findByStatus(Driver.DriverStatus status, Pageable pageable);
    Page<Driver> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);
    Page<Driver> findByLicenseType(Driver.LicenseType licenseType, Pageable pageable);


}
