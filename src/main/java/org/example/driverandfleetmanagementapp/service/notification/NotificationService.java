package org.example.driverandfleetmanagementapp.service.notification;

import org.example.driverandfleetmanagementapp.dto.notification.InspectionReminderResponse;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.springframework.data.domain.Pageable;
import java.util.concurrent.CompletableFuture;


public interface NotificationService {

    /**
     * Retrieves vehicles with upcoming technical inspections and triggers async notifications
     * @param days number of days to look ahead for inspections
     * @param pageable pagination parameters
     * @return response containing vehicles and processing status
     */
    InspectionReminderResponse processInspectionReminders(int days, Pageable pageable);


    /**
     * Asynchronously processes inspection reminder notification for a vehicle
     * @param vehicle vehicle requiring inspection notification
     * @return CompletableFuture that completes when notification is processed
     */
    CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle);

}
