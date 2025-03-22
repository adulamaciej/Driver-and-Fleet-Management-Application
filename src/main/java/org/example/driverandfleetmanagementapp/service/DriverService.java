package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;


public interface DriverService {

    DriverDto getDriverById(Integer id);
    DriverDto getDriverByLicenseNumber(String licenseNumber);


    Page<DriverDto> getAllDrivers(int page, int size);
    Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, int page, int size);
    Page<DriverDto> getDriversByName(String firstName, String lastName, int page, int size);
    Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, int page, int size);

    DriverDto createDriver(DriverDto driverDTO);
    DriverDto updateDriver(Integer id, DriverDto driverDTO);
    void deleteDriver(Integer id);

    DriverDto assignVehicleToDriver(Integer driverId, Integer vehicleId);
    DriverDto removeVehicleFromDriver(Integer driverId, Integer vehicleId);
}