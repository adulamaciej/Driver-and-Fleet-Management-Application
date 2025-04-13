package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NotificationServiceImplTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Vehicle vehicle;


    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .technicalInspectionDate(LocalDate.now().plusDays(15))
                .build();
    }

    @Test
    void processInspectionReminders_WhenVehiclesExist_ShouldReturnCorrectResponse() {
        when(vehicleService.getVehiclesWithUpcomingInspection(30)).thenReturn(List.of(vehicle));

        Map<String, Object> response = notificationService.processInspectionReminders(30);

        assertThat(response.get("message")).isEqualTo("Processing 1 inspection reminder notifications");
        assertThat(response.get("vehicles")).asList().hasSize(1);
    }

    @Test
    void processInspectionReminders_WhenNoVehicles_ShouldReturnNoVehiclesMessage() {
        when(vehicleService.getVehiclesWithUpcomingInspection(30)).thenReturn(Collections.emptyList());

        Map<String, Object> response = notificationService.processInspectionReminders(30);

        assertThat(response).containsKey("message");
        assertThat(response.get("message")).isEqualTo("No vehicles with upcoming inspections found");
    }

    @Test
    void sendInspectionReminderNotification_ReturnsNonNullFuture() {
        CompletableFuture<Void> future = notificationService.sendInspectionReminderNotification(vehicle);

        assertThat(future).isNotNull();
    }

    @Test
    void sendInspectionReminderNotification_WhenInterrupted_ShouldReturnFailedFuture() {
        Thread.currentThread().interrupt();

        CompletableFuture<Void> future = notificationService.sendInspectionReminderNotification(vehicle);

        assertThat(future).isCompletedExceptionally();

        boolean wasInterrupted = Thread.interrupted();
        assertThat(wasInterrupted).isTrue();
    }
}