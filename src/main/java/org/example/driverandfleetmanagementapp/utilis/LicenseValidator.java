package org.example.driverandfleetmanagementapp.utilis;


import org.example.driverandfleetmanagementapp.exception.BusinessLogicException;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;


public class LicenseValidator {

    public static boolean canDriverOperateVehicle(Driver.LicenseType licenseType, Vehicle.VehicleType vehicleType) {

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
        };
    }
}
