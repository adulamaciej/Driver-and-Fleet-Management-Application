package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface VehicleService {


    VehicleDto getVehicleById(Integer id);
    VehicleDto getVehicleByLicensePlate(String licensePlate);
    List<VehicleDto> getVehiclesByDriverId(Integer driverId);


    Page<VehicleDto> getAllVehicles(Pageable pageable);
    Page<VehicleDto> getVehiclesByStatus(Vehicle.VehicleStatus status, Pageable pageable);
    Page<VehicleDto> getVehiclesByBrandAndModel(String brand, String model, Pageable pageable);
    Page<VehicleDto> getVehiclesByType(Vehicle.VehicleType type, Pageable pageable);


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