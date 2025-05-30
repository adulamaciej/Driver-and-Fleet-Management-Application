package org.example.driverandfleetmanagementapp.controller;


import org.example.driverandfleetmanagementapp.dto.notification.InspectionReminderResponse;
import org.example.driverandfleetmanagementapp.dto.notification.VehicleInspectionDto;
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
import java.util.Collections;
import java.util.List;

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
        InspectionReminderResponse mockResponse = InspectionReminderResponse.builder()
                .message("Processing 2 inspection reminder notifications")
                .totalVehicles(2L)
                .vehicles(List.of(
                        VehicleInspectionDto.builder()
                                .id(1L)
                                .licensePlate("ABC123")
                                .brand("Toyota")
                                .model("Corolla")
                                .inspectionDate("2025-07-15")
                                .build(),
                        VehicleInspectionDto.builder()
                                .id(2L)
                                .licensePlate("XYZ456")
                                .brand("Ford")
                                .model("Focus")
                                .inspectionDate("2025-08-20")
                                .build()
                ))
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        when(notificationService.processInspectionReminders(30, pageRequest)).thenReturn(mockResponse);

        ResponseEntity<InspectionReminderResponse> response = notificationController.sendInspectionReminders(
                30, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody().getVehicles()).hasSize(2);
        assertThat(response.getBody().getMessage()).isEqualTo("Processing 2 inspection reminder notifications");
        assertThat(response.getBody().getTotalVehicles()).isEqualTo(2);
    }


    @Test
    void sendInspectionReminders_WhenNoVehicles_ShouldReturnAcceptedResponse(){
        InspectionReminderResponse mockResponse = InspectionReminderResponse.builder()
                .message("No vehicles with upcoming inspections found")
                .totalVehicles(0L)
                .vehicles(Collections.emptyList())
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        when(notificationService.processInspectionReminders(30, pageRequest)).thenReturn(mockResponse);

        ResponseEntity<InspectionReminderResponse> response = notificationController.sendInspectionReminders(
                30, 0, 10, "id", Sort.Direction.ASC);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody().getMessage()).isEqualTo("No vehicles with upcoming inspections found");
        assertThat(response.getBody().getTotalVehicles()).isEqualTo(0);
        assertThat(response.getBody().getVehicles()).isEmpty();
    }


}