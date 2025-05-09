package org.example.driverandfleetmanagementapp.service.notification;

import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final VehicleRepository vehicleRepository;


    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> processInspectionReminders(int days, Pageable pageable) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        Page<Vehicle> vehiclesPage = vehicleRepository.findByTechnicalInspectionDateBetween(today, endDate, pageable);


        if (vehiclesPage.isEmpty()) {
            return Map.of("message", "No vehicles with upcoming inspections found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Processing " + vehiclesPage.getTotalElements() + " inspection reminder notifications");


        List<Map<String, Object>> vehicleInfos = vehiclesPage.getContent().stream()
                .map(vehicle -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("id", vehicle.getId());
                    info.put("licensePlate", vehicle.getLicensePlate());
                    info.put("brand", vehicle.getBrand());
                    info.put("model", vehicle.getModel());
                    info.put("inspectionDate", vehicle.getTechnicalInspectionDate().toString());
                    return info;
                })
                .toList();

        response.put("vehicles", vehicleInfos);
        vehiclesPage.forEach(this::sendInspectionReminderNotification);

        return response;
    }

    @Override
    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle) {

        try {
            Thread.sleep(2000);
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}


