package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    void sendInspectionReminders_WhenVehiclesExist_ShouldReturnAcceptedResponse(){
        List<Map<String, Object>> vehicles = List.of(
                Map.of("id", 1, "licensePlate", "ABC123"),
                Map.of("id", 2, "licensePlate", "XYZ456")
        );

        Map<String, Object> mockResponse = Map.of(
                "message", "Processing 2 inspection reminder notifications",
                "vehicles", vehicles
        );

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(notificationService.processInspectionReminders(30, pageRequest)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(
                30, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(Objects.requireNonNull(response.getBody()).get("vehicles")).asList().hasSize(2);
        assertThat(response.getBody().get("message")).isEqualTo("Processing 2 inspection reminder notifications");
    }

    @Test
    void sendInspectionReminders_WhenNoVehicles_ShouldReturnAcceptedResponse(){
        Map<String, Object> mockResponse = Map.of("message", "No vehicles with upcoming inspections found");

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(notificationService.processInspectionReminders(30, pageRequest)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(mockResponse);
        assertThat(Objects.requireNonNull(response.getBody()).get("message")).isEqualTo("No vehicles with upcoming inspections found");
    }


}