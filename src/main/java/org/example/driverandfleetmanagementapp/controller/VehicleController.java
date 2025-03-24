package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
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
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get all vehicles");
        log.debug("REST request to get all vehicles, page: {}, size: {}, method=getAllVehicles", page, size);
        return ResponseEntity.ok(vehicleService.getAllVehicles(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by ID")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> getVehicleById(
            @PathVariable Integer id) {
        log.info("Get vehicle by ID");
        log.debug("REST request to get vehicle with ID: {}, method=getVehicleById", id);
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/plate/{licensePlate}")
    @Operation(summary = "Get vehicle by license plate", description = "Retrieves a specific vehicle by license plate")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> getVehicleByLicensePlate(
            @PathVariable String licensePlate) {
        log.info("Get vehicle by license plate");
        log.debug("REST request to get vehicle with license plate: {}, method=getVehicleByLicensePlate", licensePlate);
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
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get vehicles by status");
        log.debug("REST request to get vehicles with status: {}, page: {}, size: {}", status, page, size);
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status, page, size));
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
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search vehicles by brand and model");
        log.debug("REST request to search vehicles with brand: {} and model: {}, page: {}, size: {}",
                brand, model, page, size);
        return ResponseEntity.ok(vehicleService.getVehiclesByBrandAndModel(brand, model, page, size));
    }


    @GetMapping("/type/{type}")
    @Operation(summary = "Get vehicles by type", description = "Retrieves vehicles by their type with pagination")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid vehicle type or pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<VehicleDto>> getVehiclesByType(
            @PathVariable Vehicle.VehicleType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get vehicles by type");
        log.debug("REST request to get vehicles with type: {}, page: {}, size: {}", type, page, size);
        return ResponseEntity.ok(vehicleService.getVehiclesByType(type, page, size));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get vehicles by driver ID", description = "Retrieves vehicles assigned to a specific driver")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<List<VehicleDto>> getVehiclesByDriverId(
            @PathVariable Integer driverId) {
        log.info("Getting vehicles by driver id");
        log.debug("REST request to get vehicles for driver with ID: {}, method=getVehiclesByDriverId", driverId);
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
        log.info("Creating vehicle");
        log.debug("REST request to create a new vehicle: {}, method=createVehicle", vehicleDto);
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
            @PathVariable Integer id,
            @Valid @RequestBody VehicleDto vehicleDto) {
        log.info("Update vehicle");
        log.debug("REST request to update vehicle with ID: {}, method=updateVehicle", id);
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle", description = "Deletes a vehicle entry by ID")
    @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Integer id) {
        log.info("Delete vehicle");
        log.debug("REST request to delete vehicle with ID: {}, method=deleteVehicle", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{vehicleId}/driver/{driverId}")
    @Operation(summary = "Assign driver to vehicle", description = "Assigns a driver to a vehicle")
    @ApiResponse(responseCode = "200", description = "Driver assigned successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    @ApiResponse(responseCode = "409", description = "Vehicle already has an assigned driver")
    public ResponseEntity<VehicleDto> assignDriverToVehicle(
            @PathVariable Integer vehicleId,
            @PathVariable Integer driverId) {
        log.info("Assign driver to vehicle");
        log.debug("REST request to assign driver with ID: {} to vehicle with ID: {}, method=assignDriverToVehicle", driverId, vehicleId);
        return ResponseEntity.ok(vehicleService.assignDriverToVehicle(vehicleId, driverId));
    }

    @DeleteMapping("/{vehicleId}/driver")
    @Operation(summary = "Remove driver from vehicle", description = "Removes the assigned driver from a vehicle")
    @ApiResponse(responseCode = "200", description = "Driver removed successfully")
    @ApiResponse(responseCode = "400", description = "Vehicle has no assigned driver")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> removeDriverFromVehicle(
            @PathVariable Integer vehicleId) {
        log.info("Remove driver from vehicle");
        log.debug("REST request to remove driver from vehicle with ID: {}, method=removeDriverFromVehicle", vehicleId);
        return ResponseEntity.ok(vehicleService.removeDriverFromVehicle(vehicleId));
    }

    @PatchMapping("/{id}/mileage")
    @Operation(summary = "Update vehicle mileage", description = "Updates the mileage of a vehicle")
    @ApiResponse(responseCode = "200", description = "Mileage updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid mileage value")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> updateVehicleMileage(
            @PathVariable Integer id,
            @RequestParam Double mileage) {
        log.info("Updating vehicle mileage");
        log.debug("REST request to update mileage for vehicle with ID: {} to {}, method=updateVehicleMileage", id, mileage);
        return ResponseEntity.ok(vehicleService.updateVehicleMileage(id, mileage));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update vehicle status", description = "Updates the status of a vehicle")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status change")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<VehicleDto> updateVehicleStatus(
            @PathVariable Integer id,
            @RequestParam Vehicle.VehicleStatus status) {
        log.info("Updating vehicle status");
        log.debug("REST request to update status for vehicle with ID: {} to {}, method=updateVehicleStatus", id, status);
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status));
    }
}