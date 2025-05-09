package org.example.driverandfleetmanagementapp.service.vehicle;

import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;


public interface VehicleService {


    VehicleDto getVehicleById(Long id);
    VehicleDto getVehicleByLicensePlate(String licensePlate);
    Set<VehicleDto> getVehiclesByDriverId(Long driverId);

    Page<VehicleDto> getAllVehicles(Pageable pageable);
    Page<VehicleDto> getVehiclesByStatus(Vehicle.VehicleStatus status, Pageable pageable);
    Page<VehicleDto> getVehiclesByBrandAndModel(String brand, String model, Pageable pageable);
    Page<VehicleDto> getVehiclesByType(Vehicle.VehicleType type, Pageable pageable);

    VehicleDto createVehicle(VehicleDto vehicleDTO);
    VehicleDto updateVehicle(Long id, VehicleDto vehicleDTO);
    VehicleDto updateVehicleMileage(Long id, Double mileage);
    VehicleDto updateVehicleStatus(Long id, Vehicle.VehicleStatus status);
    void deleteVehicle(Long id);

}