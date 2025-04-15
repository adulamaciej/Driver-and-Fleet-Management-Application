package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DriverService {

    DriverDto getDriverById(Integer id);
    DriverDto getDriverByLicenseNumber(String licenseNumber);


    Page<DriverDto> getAllDrivers(Pageable pageable);
    Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, Pageable pageable);
    List<DriverDto> getDriversByName(String firstName, String lastName);
    Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, Pageable pageable);


    DriverDto updateDriverStatus(Integer id, Driver.DriverStatus status);
    DriverDto createDriver(DriverDto driverDTO);
    DriverDto updateDriver(Integer id, DriverDto driverDTO);
    void deleteDriver(Integer id);


    DriverDto assignVehicleToDriver(Integer driverId, Integer vehicleId);
    DriverDto removeVehicleFromDriver(Integer driverId, Integer vehicleId);
}