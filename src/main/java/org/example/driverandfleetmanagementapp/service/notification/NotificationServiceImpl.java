package org.example.driverandfleetmanagementapp.service.notification;

import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.dto.notification.InspectionReminderResponse;
import org.example.driverandfleetmanagementapp.dto.notification.VehicleInspectionDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final VehicleRepository vehicleRepository;


    @Override
    @Transactional(readOnly = true)
    public InspectionReminderResponse  processInspectionReminders(int days, Pageable pageable) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        Page<Vehicle> vehiclesPage = vehicleRepository.findByTechnicalInspectionDateBetween(today, endDate, pageable);

        if (vehiclesPage.isEmpty()) {
            return InspectionReminderResponse.builder()
                    .message("No vehicles with upcoming inspections found")
                    .totalVehicles(0L)
                    .vehicles(Collections.emptyList())
                    .build();
        }

        List<VehicleInspectionDto> vehicleInfos = vehiclesPage.getContent().stream()
                .map(vehicle -> VehicleInspectionDto.builder()
                        .id(vehicle.getId())
                        .licensePlate(vehicle.getLicensePlate())
                        .brand(vehicle.getBrand())
                        .model(vehicle.getModel())
                        .inspectionDate(vehicle.getTechnicalInspectionDate().toString())
                        .build())
                .toList();

        vehiclesPage.forEach(this::sendInspectionReminderNotification);

        return InspectionReminderResponse.builder()
                .message("Processing " + vehiclesPage.getTotalElements() + " inspection reminder notifications")
                .totalVehicles(vehiclesPage.getTotalElements())
                .vehicles(vehicleInfos)
                .build();
    }


    @Override
    @Async("taskExecutor")
    @Transactional(readOnly = true) // purpose is to show a 2s simulation
    public CompletableFuture<Void> sendInspectionReminderNotification(Vehicle vehicle) {
        try {
            Thread.sleep(2000);
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException e) {
            return CompletableFuture.failedFuture(e);
        }

    }
}


