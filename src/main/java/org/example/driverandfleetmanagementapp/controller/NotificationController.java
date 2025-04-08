package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driverandfleetmanagementapp.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "APIs for handling notifications")
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/inspection-reminders")
    public ResponseEntity<Map<String, Object>> sendInspectionReminders(@RequestParam(defaultValue = "30") int days) {
        log.info("Sending inspection reminders");
        log.debug("REST request to send inspection reminders for vehicles with inspections in the next {} days, method=sendInspectionReminders", days);

        Map<String, Object> response = notificationService.processInspectionReminders(days);

        return ResponseEntity.accepted().body(response);
    }
}
