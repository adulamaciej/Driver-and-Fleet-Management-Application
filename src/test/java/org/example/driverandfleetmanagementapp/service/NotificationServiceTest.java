package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;


    @Test
    void sendInspectionReminderNotification_ReturnsNonNullFuture() {
        Vehicle vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .technicalInspectionDate(LocalDate.now().plusDays(15))
                .build();

        CompletableFuture<Void> future = notificationService.sendInspectionReminderNotification(vehicle);

        assertThat(future).isNotNull();
    }

    @Test
    void sendInspectionReminderNotification_WhenInterrupted_ShouldReturnFailedFuture() {
        Vehicle vehicle = Vehicle.builder()
                .id(1)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .technicalInspectionDate(LocalDate.now().plusDays(15))
                .build();

        Thread.currentThread().interrupt();

        CompletableFuture<Void> future = notificationService.sendInspectionReminderNotification(vehicle);

        assertThat(future).isCompletedExceptionally();

        boolean wasInterrupted = Thread.interrupted();
        assertThat(wasInterrupted).isTrue();
    }
}