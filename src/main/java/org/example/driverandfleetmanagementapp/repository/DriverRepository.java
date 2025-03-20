package org.example.driverandfleetmanagementapp.repository;

import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Integer> {
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    List<Driver> findByStatus(Driver.DriverStatus status);
    List<Driver> findByFirstNameAndLastName(String firstName, String lastName);
    List<Driver> findByLicenseType(Driver.LicenseType licenseType);
}
