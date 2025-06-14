package org.example.driverandfleetmanagementapp.service;

import org.example.driverandfleetmanagementapp.dto.notification.InspectionReminderResponse;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.example.driverandfleetmanagementapp.service.notification.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.*;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NotificationServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Vehicle vehicle;
    private Pageable pageable;


    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .id(1L)
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .technicalInspectionDate(LocalDate.now().plusDays(15))
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void processInspectionReminders_WhenVehiclesExist_ShouldReturnCorrectResponse() {
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(vehicle), pageable, 1);

        when(vehicleRepository.findByTechnicalInspectionDateBetween(any(), any(), any()))
                .thenReturn(vehiclePage);

        InspectionReminderResponse response = notificationService.processInspectionReminders(30, pageable);

        assertThat(response.getMessage()).isEqualTo("Processing 1 inspection reminder notifications");
        assertThat(response.getVehicles()).hasSize(1);
        assertThat(response.getTotalVehicles()).isEqualTo(1);
        assertThat(response.getVehicles().getFirst().getLicensePlate()).isEqualTo("ABC123");
    }

    @Test
    void processInspectionReminders_WhenNoVehicles_ShouldReturnNoVehiclesMessage() {
        Page<Vehicle> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(vehicleRepository.findByTechnicalInspectionDateBetween(any(), any(), any()))
                .thenReturn(emptyPage);

        InspectionReminderResponse  response = notificationService.processInspectionReminders(30, pageable);

        assertThat(response.getMessage()).isEqualTo("No vehicles with upcoming inspections found");
        assertThat(response.getTotalVehicles()).isEqualTo(0);
        assertThat(response.getVehicles()).isEmpty();
    }

    @Test
    void sendInspectionReminderNotification_ReturnsNonNullFuture() {
        CompletableFuture<Void> future = notificationService.sendInspectionReminderNotification(vehicle);

        assertThat(future).isNotNull();
    }


}