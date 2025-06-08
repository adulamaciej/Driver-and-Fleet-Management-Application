package org.example.driverandfleetmanagementapp.service.driver;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DriverService {

    DriverDto getDriverById(Long id);
    DriverDto getDriverByLicenseNumber(String licenseNumber);
    DriverDto getDriverByVehicleId(Long vehicleId);

    Page<DriverDto> getDriversByFirstAndLastName(String firstName, String lastName, Pageable pageable);
    Page<DriverDto> getAllDrivers(Pageable pageable);
    Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, Pageable pageable);
    Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, Pageable pageable);

    DriverDto createDriver(DriverDto driverDTO);
    DriverDto updateDriver(Long id, DriverDto driverDTO);
    DriverDto updateDriverStatus(Long id, Driver.DriverStatus status);
    void deleteDriver(Long id);


}