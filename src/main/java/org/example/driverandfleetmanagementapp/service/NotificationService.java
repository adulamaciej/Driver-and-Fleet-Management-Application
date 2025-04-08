package org.example.driverandfleetmanagementapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final VehicleService vehicleService;

    public Map<String, Object> processInspectionReminders(int days) {
        List<Vehicle> vehicles = vehicleService.getVehiclesWithUpcomingInspection(days);

        if (vehicles.isEmpty()) {
            return Map.of("message", "No vehicles with upcoming inspections found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Processing " + vehicles.size() + " inspection reminder notifications");

        List<Map<String, Object>> vehicleInfos = vehicles.stream()
                .map(this::createVehicleInfo)
                .toList();

        response.put("vehicles", vehicleInfos);

        List<CompletableFuture<Void>> futures = vehicles.stream()
                .map(this::sendInspectionReminderNotification)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("Error processing some notifications", ex);
                    return null;
                });

        return response;
    }

    private Map<String, Object> createVehicleInfo(Vehicle vehicle) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", vehicle.getId());
        info.put("licensePlate", vehicle.getLicensePlate());
        info.put("brand", vehicle.getBrand());
        info.put("model", vehicle.getModel());
        info.put("inspectionDate", vehicle.getTechnicalInspectionDate().toString());
        return info;
    }


    @Async("taskExecutor")
    public CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle){
        log.info("Sending technical inspection notification");
        log.debug("Preparing technical inspection reminder for vehicle: {}, method=sendInspectionReminderNotification", vehicle.getLicensePlate());

        try {
            Thread.sleep(2000);

            log.info("Notification sent for vehicle {} with inspection date: {}",
                    vehicle.getLicensePlate(), vehicle.getTechnicalInspectionDate());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while processing notification for vehicle {}", vehicle.getLicensePlate(), e);
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(null);
    }
}


