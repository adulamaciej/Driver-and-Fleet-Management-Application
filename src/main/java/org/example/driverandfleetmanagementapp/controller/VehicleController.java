package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.vehicle.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;


@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management", description = "APIs for managing vehicles")
public class VehicleController {

    private final VehicleService vehicleService;


    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Retrieves paginated list of vehicles")
    @ApiResponse(responseCode = "200", description = "List of vehicles retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<VehicleDto>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(PageRequest.of(page, size, Sort.by(sortDirection, sortBy))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by ID")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> getVehicleById(
            @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/plate/{licensePlate}")
    @Operation(summary = "Get vehicle by license plate", description = "Retrieves a specific vehicle by license plate")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> getVehicleByLicensePlate(
            @PathVariable String licensePlate) {
        return ResponseEntity.ok(vehicleService.getVehicleByLicensePlate(licensePlate));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get vehicles by status", description = "Retrieves vehicles by their status with pagination")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid vehicle status or pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<VehicleDto>> getVehiclesByStatus(
            @PathVariable Vehicle.VehicleStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status,
                PageRequest.of(page, size,Sort.by(sortDirection, sortBy))
        ));
    }

    @GetMapping("/search")
    @Operation(summary = "Search vehicles by brand and model", description = "Searches for vehicles by brand and model with pagination")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters or pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<VehicleDto>> searchVehiclesByBrandAndModel(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(vehicleService.getVehiclesByBrandAndModel(brand, model,
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        ));
    }


    @GetMapping("/type/{type}")
    @Operation(summary = "Get vehicles by type", description = "Retrieves vehicles by their type with pagination")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid vehicle type or pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<VehicleDto>> getVehiclesByType(
            @PathVariable Vehicle.VehicleType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(vehicleService.getVehiclesByType(type,
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        ));
    }


    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get vehicles by driver ID", description = "Retrieves vehicles assigned to a specific driver")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<Set<VehicleDto>> getVehiclesByDriverId(
            @PathVariable Long driverId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByDriverId(driverId));
    }

    @PostMapping
    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle entry")
    @ApiResponse(responseCode = "201", description = "Vehicle created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "409", description = "Vehicle already exists")
    public ResponseEntity<VehicleDto> createVehicle(
            @Valid @RequestBody VehicleDto vehicleDto) {
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vehicle", description = "Updates a vehicle entry by ID")
    @ApiResponse(responseCode = "200", description = "Vehicle updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @ApiResponse(responseCode = "409", description = "License plate conflict")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDto vehicleDto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle", description = "Deletes a vehicle entry by ID")
    @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/mileage")
    @Operation(summary = "Update vehicle mileage", description = "Updates the mileage of a vehicle")
    @ApiResponse(responseCode = "200", description = "Mileage updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid mileage value")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> updateVehicleMileage(
            @PathVariable Long id,
            @RequestBody Double mileage) {
        return ResponseEntity.ok(vehicleService.updateVehicleMileage(id, mileage));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update vehicle status", description = "Updates the status of a vehicle")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status change")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> updateVehicleStatus(
            @PathVariable Long id,
            @RequestBody Vehicle.VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }
}