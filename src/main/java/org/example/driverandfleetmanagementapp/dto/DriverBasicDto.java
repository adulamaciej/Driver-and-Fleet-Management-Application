package org.example.driverandfleetmanagementapp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverBasicDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String licenseNumber;

}
