package org.example.driverandfleetmanagementapp.controller;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.service.NotificationService;
import org.example.driverandfleetmanagementapp.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private List<Vehicle> vehicles;

    @BeforeEach
    void setUp() {
        Vehicle vehicle1 = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .technicalInspectionDate(LocalDate.now().plusDays(15))
                .build();

        Vehicle vehicle2 = Vehicle.builder()
                .id(2)
                .licensePlate("XYZ789")
                .brand("Honda")
                .model("Civic")
                .technicalInspectionDate(LocalDate.now().plusDays(25))
                .build();

        vehicles = List.of(vehicle1, vehicle2);
    }

    @Test
    void sendInspectionReminders_WhenVehiclesFound_ShouldReturnVehiclesAndStartAsyncProcessing() {
        when(vehicleService.getVehiclesWithUpcomingInspection(anyInt())).thenReturn(vehicles);
        when(notificationService.sendInspectionReminderNotification(any(Vehicle.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("Processing 2 inspection reminder notifications");

        List<?> vehicleInfos = (List<?>) body.get("vehicles");
        assertThat(vehicleInfos).hasSize(2);

        verify(notificationService, times(2)).sendInspectionReminderNotification(any(Vehicle.class));
    }

    @Test
    void sendInspectionReminders_WhenNoVehiclesFound_ShouldReturnAppropriateMessage() {
        when(vehicleService.getVehiclesWithUpcomingInspection(anyInt())).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("message")).isEqualTo("No vehicles with upcoming inspections found");

        verify(notificationService, never()).sendInspectionReminderNotification(any(Vehicle.class));
    }
    @Test
    void sendInspectionReminders_WhenNotificationFails_ShouldHandleExceptionAndReturnAccepted() {
        when(vehicleService.getVehiclesWithUpcomingInspection(anyInt())).thenReturn(vehicles);
        when(notificationService.sendInspectionReminderNotification(any(Vehicle.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test exception")));

        ResponseEntity<Map<String, Object>> response = notificationController.sendInspectionReminders(30);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}