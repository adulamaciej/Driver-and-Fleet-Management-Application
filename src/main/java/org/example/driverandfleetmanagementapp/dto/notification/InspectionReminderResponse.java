package org.example.driverandfleetmanagementapp.dto.notification;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InspectionReminderResponse {
    private String message;
    private Long totalVehicles;
    private List<VehicleInspectionDto> vehicles;
}
