package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;


    @Test
    void sendInspectionReminders_WhenVehiclesExist_ShouldReturnAcceptedResponse() {
        Map<String, Object> mockResponse = Map.of("message", "Processing 2 inspection reminder notifications",
                "vehicles", List.of(Map.of("id", 1), Map.of("id", 2)));

        when(notificationService.processInspectionReminders(30)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }

    @Test
    void sendInspectionReminders_WhenNoVehicles_ShouldReturnAcceptedResponse() {
        Map<String, Object> mockResponse = Map.of("message", "No vehicles with upcoming inspections found");

        when(notificationService.processInspectionReminders(30)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}