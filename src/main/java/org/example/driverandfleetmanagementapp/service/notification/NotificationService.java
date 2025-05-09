package org.example.driverandfleetmanagementapp.service.notification;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface NotificationService {

    Map<String,Object> processInspectionReminders(int days, Pageable pageable);
    //method retrieves vehicles with upcoming technical inspections within
    // a specified time period


    CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle);
    // asynchronously  sends a notification for a single
    // vehicle that requires a technical inspection

}
