package org.example.driverandfleetmanagementapp.service.assignment;


import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.audit.Auditable;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
import org.example.driverandfleetmanagementapp.mapper.DriverMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.example.driverandfleetmanagementapp.utilis.LicenseValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServiceImpl implements AssignmentService {


    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverMapper driverMapper;



    @Override
    @Timed("fleet.assignment.create.time")
    @Auditable(entity = "VEHICLE", action = "DRIVER_ASSIGNMENT")
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #driverId"),
            @CacheEvict(value = "vehicles", key = "'vehicle:' + #vehicleId"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #driverId"),
            @CacheEvict(value = "drivers", key = "'vehicle:' + #vehicleId")
    })
    public DriverDto assignVehicleToDriver(Long driverId, Long vehicleId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + driverId + " not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + vehicleId + " not found"));


        if (driver.getStatus() != Driver.DriverStatus.ACTIVE) {
            throw new BusinessLogicException("Cannot assign vehicle to driver with status " + driver.getStatus() +
                    ". Driver must have ACTIVE status.");
        }

        if (vehicle.getDriver() != null) {
            throw new ResourceConflictException("Vehicle is already assigned to a driver");
        }

        if (driver.getVehicles().size() >= 2) {
            throw new BusinessLogicException("Driver cannot have more than 2 vehicles assigned");
        }

        if (!LicenseValidator.canDriverOperateVehicle(driver.getLicenseType(), vehicle.getType())) {
            throw new BusinessLogicException("Driver's license type " + driver.getLicenseType() +
                    " does not allow operating vehicle of type " + vehicle.getType());
        }

        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);
        vehicle.setStatus(Vehicle.VehicleStatus.IN_USE);
        return driverMapper.toDto(driver);
    }




    @Override
    @Timed("fleet.assignment.remove.time")
    @Auditable(entity = "VEHICLE", action = "DRIVER_UNASSIGNMENT")
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #driverId"),
            @CacheEvict(value = "vehicles", key = "'vehicle:' + #vehicleId"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #driverId"),
            @CacheEvict(value = "drivers", key = "'vehicle:' + #vehicleId")
    })
    public DriverDto removeVehicleFromDriver(Long driverId, Long vehicleId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + driverId + " not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + vehicleId + " not found"));


        if (vehicle.getDriver() != null && !vehicle.getDriver().getId().equals(driver.getId())) {
            throw new BusinessLogicException("Vehicle is assigned to a different driver (ID: " +
                    vehicle.getDriver().getId() + ")");
        }

        driver.getVehicles().remove(vehicle);
        vehicle.setDriver(null);
        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);

        if (driver.getVehicles().isEmpty()) {
            driver.setStatus(Driver.DriverStatus.INACTIVE);
        }


        return driverMapper.toDto(driver);
    }

}
