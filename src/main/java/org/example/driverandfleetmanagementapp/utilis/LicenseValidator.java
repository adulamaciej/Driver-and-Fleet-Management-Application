package org.example.driverandfleetmanagementapp.utilis;

import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;

@Slf4j
public class LicenseValidator {

    public static boolean canDriverOperateVehicle(Driver.LicenseType licenseType, Vehicle.VehicleType vehicleType) {
        log.debug("Checking if license type {} can operate vehicle type {}", licenseType, vehicleType);

        if (licenseType == null) {
            throw new BusinessLogicException("Driver has an unknown or invalid license type");
        }

        return switch (licenseType) {
            case B -> vehicleType == Vehicle.VehicleType.CAR;
            case C, CE -> vehicleType == Vehicle.VehicleType.CAR ||
                    vehicleType == Vehicle.VehicleType.VAN ||
                    vehicleType == Vehicle.VehicleType.TRUCK;
            case D, DE -> vehicleType == Vehicle.VehicleType.CAR ||
                    vehicleType == Vehicle.VehicleType.BUS;
            default -> false;
        };
    }
}
