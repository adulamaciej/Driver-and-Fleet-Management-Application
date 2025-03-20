package org.example.driverandfleetmanagementapp.service;


import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;

import java.util.List;



public interface VehicleService {

    Page<VehicleDto> getAllVehicles(int page, int size);

    VehicleDto getVehicleById(Integer id);

    VehicleDto getVehicleByLicensePlate(String licensePlate);

    List<VehicleDto> getVehiclesByStatus(Vehicle.VehicleStatus status);

    List<VehicleDto> getVehiclesByBrandAndModel(String brand, String model);

    List<VehicleDto> getVehiclesByType(Vehicle.VehicleType type);

    List<VehicleDto> getVehiclesByDriverId(Integer driverId);

    VehicleDto createVehicle(VehicleDto vehicleDTO);

    VehicleDto updateVehicle(Integer id, VehicleDto vehicleDTO);

    void deleteVehicle(Integer id);

    VehicleDto assignVehicleToDriver(Integer vehicleId, Integer driverId);

    VehicleDto removeDriverFromVehicle(Integer vehicleId);

    VehicleDto updateVehicleMileage(Integer id, Double mileage);

    VehicleDto updateVehicleStatus(Integer id, Vehicle.VehicleStatus status);

    void validateTechnicalInspectionDate(Vehicle vehicle);
}