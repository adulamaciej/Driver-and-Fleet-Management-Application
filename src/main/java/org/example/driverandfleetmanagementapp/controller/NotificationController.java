package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.NotificationService;
import org.example.driverandfleetmanagementapp.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "APIs for handling notifications")
public class NotificationController {

    private final VehicleService vehicleService;
    private final NotificationService notificationService;


    @PostMapping("/inspection-reminders")
    public ResponseEntity<Map<String, Object>> sendInspectionReminders(@RequestParam(defaultValue = "30") int days) {
        log.info("Sending inspection reminders");
        log.debug("REST request to send inspection reminders for vehicles with inspections in the next {} days, method=sendInspectionReminders", days);

        List<Vehicle> vehicles = vehicleService.getVehiclesWithUpcomingInspection(days);

        if (vehicles.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No vehicles with upcoming inspections found"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Processing %d inspection reminder notifications", vehicles.size()));

        List<Map<String, Object>> vehicleInfos = vehicles.stream()
                .map(vehicle -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", vehicle.getId());
                    info.put("licensePlate", vehicle.getLicensePlate());
                    info.put("brand", vehicle.getBrand());
                    info.put("model", vehicle.getModel());
                    info.put("inspectionDate", vehicle.getTechnicalInspectionDate().toString());
                    return info;
                })
                .toList();

        response.put("vehicles", vehicleInfos);

        List<CompletableFuture<Void>> futures = vehicles.stream()
                .map(notificationService::sendInspectionReminderNotification)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("Error processing some notifications", ex);
                    return null;
                });

        return ResponseEntity.accepted().body(response);
    }
}
