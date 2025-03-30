package org.example.driverandfleetmanagementapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class NotificationService {


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


