package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs for managing drivers")
public class DriverController {

    private final DriverService driverService;


    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieves paginated list of drivers")
    @ApiResponse(responseCode = "200", description = "List of drivers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<Page<DriverDto>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(driverService.getAllDrivers(PageRequest.of(page, size, Sort.by(sortDirection, sortBy))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves a specific driver by ID")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/license/{licenseNumber}")
    @Operation(summary = "Get driver by license number", description = "Retrieves a specific driver by license number")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverByLicenseNumber(
            @PathVariable String licenseNumber) {
        return ResponseEntity.ok(driverService.getDriverByLicenseNumber(licenseNumber));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get drivers by status", description = "Retrieves paginated drivers by their status")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "400", description = "Invalid driver status or pagination parameters")
    public ResponseEntity<Page<DriverDto>> getDriversByStatus(
            @PathVariable Driver.DriverStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status, PageRequest.of(page, size, Sort.by(sortDirection, sortBy))));
    }

    @GetMapping("/search")
    @Operation(summary = "Search drivers by name", description = "Searches for drivers by first name and last name")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    public ResponseEntity<List<DriverDto>> searchDriversByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        return ResponseEntity.ok(driverService.getDriversByName(firstName, lastName));
    }

    @GetMapping("/license-type/{licenseType}")
    @Operation(summary = "Get drivers by license type", description = "Retrieves paginated drivers by their license type")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "400", description = "Invalid license type or pagination parameters")
    public ResponseEntity<Page<DriverDto>> getDriversByLicenseType(
            @PathVariable Driver.LicenseType licenseType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(driverService.getDriversByLicenseType(licenseType,  PageRequest.of(page, size, Sort.by(sortDirection, sortBy))));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update driver status", description = "Updates the status of a driver")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status change")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> updateDriverStatus(
            @PathVariable Integer id,
            @RequestBody Driver.DriverStatus status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    @PostMapping
    @Operation(summary = "Create a new driver", description = "Creates a new driver entry")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<DriverDto> createDriver(
            @Valid @RequestBody DriverDto driverDto) {
        return new ResponseEntity<>(driverService.createDriver(driverDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing driver", description = "Updates a driver entry by ID")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    @ApiResponse(responseCode = "409", description = "License number conflict")
    public ResponseEntity<DriverDto> updateDriver(
            @PathVariable Integer id,
            @Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(id, driverDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver", description = "Deletes a driver entry by ID")
    @ApiResponse(responseCode = "400", description = "Cannot delete driver with assigned vehicles")
    @ApiResponse(responseCode = "204", description = "Driver deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<Void> deleteDriver(
            @PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{driverId}/vehicle/{vehicleId}")
    @Operation(summary = "Assign vehicle to driver", description = "Assigns a vehicle to a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle assigned successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    @ApiResponse(responseCode = "409", description = "Vehicle already assigned")
    public ResponseEntity<DriverDto> assignVehicleToDriver(
            @PathVariable Integer driverId,
            @PathVariable Integer vehicleId) {
        return ResponseEntity.ok(driverService.assignVehicleToDriver(driverId, vehicleId));
    }

    @DeleteMapping("/{driverId}/vehicle/{vehicleId}")
    @Operation(summary = "Remove vehicle from driver", description = "Removes a vehicle from a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle removed successfully")
    @ApiResponse(responseCode = "400", description = "Vehicle is not assigned to this driver")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    public ResponseEntity<DriverDto> removeVehicleFromDriver(
            @PathVariable Integer driverId,
            @PathVariable Integer vehicleId) {
        return ResponseEntity.ok(driverService.removeVehicleFromDriver(driverId, vehicleId));
    }
}