package org.example.driverandfleetmanagementapp.service;

import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final VehicleService vehicleService;


    public Map<String, Object> processInspectionReminders(int days) {

            List<Vehicle> vehicles = vehicleService.getVehiclesWithUpcomingInspection(days);

            if (vehicles.isEmpty()) {
                return Map.of("message", "No vehicles with upcoming inspections found");
            }


            Map<String, Object> response = new HashMap<>();
            response.put("message", "Processing " + vehicles.size() + " inspection reminder notifications");

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
            vehicles.forEach(this::sendInspectionReminderNotification);

            return response;
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}


