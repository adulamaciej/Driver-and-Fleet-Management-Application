package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface NotificationService {
    Map<String,Object> processInspectionReminders(int days);

    CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle);


}
