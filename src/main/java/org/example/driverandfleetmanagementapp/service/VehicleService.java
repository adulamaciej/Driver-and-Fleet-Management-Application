package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import java.util.List;

public interface VehicleService {


    VehicleDto getVehicleById(Integer id);
    VehicleDto getVehicleByLicensePlate(String licensePlate);
    List<VehicleDto> getVehiclesByDriverId(Integer driverId);


    Page<VehicleDto> getAllVehicles(int page, int size);
    Page<VehicleDto> getVehiclesByStatus(Vehicle.VehicleStatus status, int page, int size);
    Page<VehicleDto> getVehiclesByBrandAndModel(String brand, String model, int page, int size);
    Page<VehicleDto> getVehiclesByType(Vehicle.VehicleType type, int page, int size);


    VehicleDto createVehicle(VehicleDto vehicleDTO);
    VehicleDto updateVehicle(Integer id, VehicleDto vehicleDTO);
    VehicleDto updateVehicleMileage(Integer id, Double mileage);
    VehicleDto updateVehicleStatus(Integer id, Vehicle.VehicleStatus status);
    void deleteVehicle(Integer id);

    VehicleDto assignDriverToVehicle(Integer vehicleId, Integer driverId);
    VehicleDto removeDriverFromVehicle(Integer vehicleId);


    void validateTechnicalInspectionDate(Vehicle vehicle);

    List<Vehicle> getVehiclesWithUpcomingInspection(int days);
}