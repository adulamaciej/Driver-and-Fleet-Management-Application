package org.example.driverandfleetmanagementapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.notification.InspectionReminderResponse;
import org.example.driverandfleetmanagementapp.service.notification.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for handling notifications")
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping("/inspection-reminders")
    @Operation(summary = "Send inspection reminders", description = "Sends notifications for vehicles with upcoming technical inspections")
    @ApiResponse(responseCode = "202", description = "Notifications queued for processing")
    public ResponseEntity<InspectionReminderResponse> sendInspectionReminders(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        InspectionReminderResponse response = notificationService.processInspectionReminders(
                days, PageRequest.of(page, size, Sort.by(sortDirection, sortBy)));
        return ResponseEntity.accepted().body(response);
    }
}
