package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for handling notifications")
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/inspection-reminders")
    @Operation(summary = "Send inspection reminders", description = "Sends notifications for vehicles with upcoming technical inspections")
    @ApiResponse(responseCode = "202", description = "Notifications queued for processing")
    public ResponseEntity<Map<String, Object>> sendInspectionReminders(@RequestParam(defaultValue = "30") int days) {
        Map<String, Object> response = notificationService.processInspectionReminders(days);
        return ResponseEntity.accepted().body(response);
    }
}
