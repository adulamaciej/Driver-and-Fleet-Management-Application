package org.example.driverandfleetmanagementapp.service.assignment;

import org.example.driverandfleetmanagementapp.dto.DriverDto;

public interface AssignmentService {

    DriverDto assignVehicleToDriver(Long driverId, Long vehicleId);
    DriverDto removeVehicleFromDriver(Long driverId, Long vehicleId);

}
