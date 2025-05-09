package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.service.assignment.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Driver-Vehicle Assignments", description = "APIs for managing assignments between drivers and vehicles")
public class AssignmentController {


    private final AssignmentService assignmentService;


    @PostMapping("/{driverId}/vehicle/{vehicleId}")
    @Operation(summary = "Assign vehicle to driver", description = "Assigns a vehicle to a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle assigned successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    @ApiResponse(responseCode = "409", description = "Vehicle already assigned")
    public ResponseEntity<DriverDto> assignVehicleToDriver(
            @PathVariable Long driverId,
            @PathVariable Long vehicleId) {
        return ResponseEntity.ok(assignmentService.assignVehicleToDriver(driverId, vehicleId));
    }


    @DeleteMapping("/{driverId}/vehicle/{vehicleId}")
    @Operation(summary = "Remove vehicle from driver", description = "Removes a vehicle from a driver")
    @ApiResponse(responseCode = "200", description = "Vehicle removed successfully")
    @ApiResponse(responseCode = "400", description = "Vehicle is not assigned to this driver")
    @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions to access resource")
    @ApiResponse(responseCode = "404", description = "Driver or vehicle not found")
    public ResponseEntity<DriverDto> removeVehicleFromDriver(
            @PathVariable Long driverId,
            @PathVariable Long vehicleId) {
        DriverDto updatedDriver = assignmentService.removeVehicleFromDriver(driverId, vehicleId);
        return ResponseEntity.ok(updatedDriver);
    }

}
