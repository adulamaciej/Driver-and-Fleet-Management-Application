package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DriverService {

    DriverDto getDriverById(Integer id);
    DriverDto getDriverByLicenseNumber(String licenseNumber);


    DriverDto updateDriverStatus(Integer id, Driver.DriverStatus status);

    Page<DriverDto> getAllDrivers(Pageable pageable);
    Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, Pageable pageable);
    Page<DriverDto> getDriversByName(String firstName, String lastName, Pageable pageable);
    Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, Pageable pageable);

    DriverDto createDriver(DriverDto driverDTO);
    DriverDto updateDriver(Integer id, DriverDto driverDTO);
    void deleteDriver(Integer id);

    DriverDto assignVehicleToDriver(Integer driverId, Integer vehicleId);
    DriverDto removeVehicleFromDriver(Integer driverId, Integer vehicleId);
}