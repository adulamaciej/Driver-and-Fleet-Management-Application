package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DriverService {

    Page<DriverDto> getAllDrivers(int page, int size);

    DriverDto getDriverById(Integer id);

    DriverDto getDriverByLicenseNumber(String licenseNumber);

    List<DriverDto> getDriversByStatus(Driver.DriverStatus status);

    List<DriverDto> getDriversByName(String firstName, String lastName);

    List<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType);

    DriverDto createDriver(DriverDto driverDTO);

    DriverDto updateDriver(Integer id, DriverDto driverDTO);

    void deleteDriver(Integer id);

    DriverDto assignVehicleToDriver(Integer driverId, Integer vehicleId);

    DriverDto removeVehicleFromDriver(Integer driverId, Integer vehicleId);
}