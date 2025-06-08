package org.example.driverandfleetmanagementapp.controller;


import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.service.driver.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs for managing drivers")
public class DriverController {

    private final DriverService driverService;


    @RateLimiter(name = "api")
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

    @RateLimiter(name = "api")
    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves a specific driver by ID")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverById(
            @PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @RateLimiter(name = "api")
    @GetMapping("/license/{licenseNumber}")
    @Operation(summary = "Get driver by license number", description = "Retrieves a specific driver by license number")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> getDriverByLicenseNumber(
            @PathVariable String licenseNumber) {
        return ResponseEntity.ok(driverService.getDriverByLicenseNumber(licenseNumber));
    }

    @RateLimiter(name = "api")
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

    @RateLimiter(name = "api")
    @GetMapping("/search")
    @Operation(summary = "Search drivers by name", description = "Searches for drivers by first name and last name")
    @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    public ResponseEntity<Page<DriverDto>> getDriversByFirstAndLastName(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        return ResponseEntity.ok(driverService.getDriversByFirstAndLastName(
                firstName, lastName,
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        ));
    }

    @RateLimiter(name = "api")
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

    @RateLimiter(name = "api")
    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get driver by vehicle ID", description = "Retrieves the driver assigned to a specific vehicle")
    @ApiResponse(responseCode = "200", description = "Driver retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Vehicle has no assigned driver")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    public ResponseEntity<DriverDto> getDriverByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(driverService.getDriverByVehicleId(vehicleId));
    }

    @RateLimiter(name = "admin-api")
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update driver status", description = "Updates the status of a driver")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status change")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<DriverDto> updateDriverStatus(
            @PathVariable Long id,
            @RequestBody Driver.DriverStatus status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(id, status));
    }

    @RateLimiter(name = "admin-api")
    @PostMapping
    @Operation(summary = "Create a new driver", description = "Creates a new driver entry")
    @ApiResponse(responseCode = "201", description = "Driver created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    public ResponseEntity<DriverDto> createDriver(
            @Valid @RequestBody DriverDto driverDto) {
        return new ResponseEntity<>(driverService.createDriver(driverDto), HttpStatus.CREATED);
    }

    @RateLimiter(name = "admin-api")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing driver", description = "Updates a driver entry by ID")
    @ApiResponse(responseCode = "200", description = "Driver updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    @ApiResponse(responseCode = "409", description = "License number conflict")
    public ResponseEntity<DriverDto> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(id, driverDto));
    }

    @RateLimiter(name = "admin-api")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver", description = "Deletes a driver entry by ID")
    @ApiResponse(responseCode = "400", description = "Cannot delete driver with assigned vehicles")
    @ApiResponse(responseCode = "204", description = "Driver deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver not found")
    public ResponseEntity<Void> deleteDriver(
            @PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

}