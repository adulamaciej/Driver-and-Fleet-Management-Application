package org.example.driverandfleetmanagementapp.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleInspectionDto {

    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private String inspectionDate;
}
