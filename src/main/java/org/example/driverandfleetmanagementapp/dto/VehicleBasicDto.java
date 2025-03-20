package org.example.driverandfleetmanagementapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.driverandfleetmanagementapp.model.Vehicle;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBasicDto {
    private Integer id;
    private String licensePlate;
    private String brand;
    private String model;
    private Vehicle.VehicleStatus status;
}
