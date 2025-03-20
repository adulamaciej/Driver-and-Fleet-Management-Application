package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Management", description = "APIs for managing drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieves paginated list of drivers")
    @ApiResponse(responseCode = "200", description = "List of drivers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    public ResponseEntity<Page<DriverDto>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all drivers");
        log.debug("REST request to get all drivers, page: {}, size: {}, method=getAllDrivers", page, size);
        return ResponseEntity.ok(driverService.getAllDrivers(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves a specific driver by ID")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverById(
            @PathVariable Integer id) {
        log.info("Getting driver by ID");
        log.debug("REST request to get driver with ID: {}, method=getDriverById", id);
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/license/{licenseNumber}")
    @Operation(summary = "Get driver by license number", description = "Retrieves a specific driver by license number")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverByLicenseNumber(
            @PathVariable String licenseNumber) {
        log.info("Getting driver by license number");
        log.debug("REST request to get driver with license number: {}, method=getDriverByLicenseNumber", licenseNumber);
        return ResponseEntity.ok(driverService.getDriverByLicenseNumber(licenseNumber));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get drivers by status", description = "Retrieves drivers by their status")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid driver status")
    public ResponseEntity<List<DriverDto>> getDriversByStatus(
            @PathVariable Driver.DriverStatus status) {
        log.info("Getting drivers by status");
        log.debug("REST request to get drivers with status: {}, method=getDriversByStatus", status);
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search drivers by name", description = "Searches for drivers by first name and last name")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    public ResponseEntity<List<DriverDto>> searchDriversByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        log.info("Search drivers by name");
        log.debug("REST request to search drivers with first name: {} and last name: {}, method=searchDriversByName", firstName, lastName);
        return ResponseEntity.ok(driverService.getDriversByName(firstName, lastName));
    }

    @GetMapping("/license-type/{licenseType}")
    @Operation(summary = "Get drivers by license type", description = "Retrieves drivers by their license type")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid license type")
    public ResponseEntity<List<DriverDto>> getDriversByLicenseType(
            @PathVariable Driver.LicenseType licenseType) {
        log.info("Getting drivers by license type");
        log.debug("REST request to get drivers with license type: {}, method=getDriversByLicenseType", licenseType);
        return ResponseEntity.ok(driverService.getDriversByLicenseType(licenseType));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new driver", description = "Creates a new driver entry")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<DriverDto> createDriver(
            @Valid @RequestBody DriverDto driverDto) {
        log.info("Creating driver");
        log.debug("REST request to create a new driver: {}, method=createDriver", driverDto);
        return new ResponseEntity<>(driverService.createDriver(driverDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing driver", description = "Updates a driver entry by ID")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    @ApiResponse(responseCode = "409", description = "License number conflict")
    public ResponseEntity<DriverDto> updateDriver(
            @PathVariable Integer id,
            @Valid @RequestBody DriverDto driverDto) {
        log.info("Updating driver");
        log.debug("REST request to update driver with ID: {}, method=updateDriver", id);
        return ResponseEntity.ok(driverService.updateDriver(id, driverDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a driver", description = "Deletes a driver entry by ID")
    @ApiResponse(responseCode = "204", description = "Driver deleted successfully")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    @ApiResponse(responseCode = "400", description = "Cannot delete driver with assigned vehicles")
    public ResponseEntity<Void> deleteDriver(
            @PathVariable Integer id) {
        log.info("Deleting driver");
        log.debug("REST request to delete driver with ID: {}, method=deleteDriver", id);
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{driverId}/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign vehicle to driver", description = "Assigns a vehicle to a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle assigned successfully")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    @ApiResponse(responseCode = "409", description = "Vehicle already assigned")
    public ResponseEntity<DriverDto> assignVehicleToDriver(
            @PathVariable Integer driverId,
            @PathVariable Integer vehicleId) {
        log.info("Assigning vehicles to driver");
        log.debug("REST request to assign vehicle with ID: {} to driver with ID: {}, method=assignVehicleToDriver", vehicleId, driverId);
        return ResponseEntity.ok(driverService.assignVehicleToDriver(driverId, vehicleId));
    }

    @DeleteMapping("/{driverId}/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove vehicle from driver", description = "Removes a vehicle from a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle removed successfully")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    @ApiResponse(responseCode = "400", description = "Vehicle is not assigned to this driver")
    public ResponseEntity<DriverDto> removeVehicleFromDriver(
            @PathVariable Integer driverId,
            @PathVariable Integer vehicleId) {
        log.info("Removing vehicle from driver");
        log.debug("REST request to remove vehicle with ID: {} from driver with ID: {}, method=removeVehicleFromDriver", vehicleId, driverId);
        return ResponseEntity.ok(driverService.removeVehicleFromDriver(driverId, vehicleId));
    }
}