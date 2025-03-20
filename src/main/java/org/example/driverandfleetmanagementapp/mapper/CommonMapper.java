package org.example.driverandfleetmanagementapp.mapper;

import org.example.driverandfleetmanagementapp.dto.DriverBasicDto;
import org.example.driverandfleetmanagementapp.dto.VehicleBasicDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    @Named("toDriverBasicDto")
    DriverBasicDto toDriverBasicDto(Driver driver);

    @Named("toVehicleBasicDto")
    VehicleBasicDto toVehicleBasicDto(Vehicle vehicle);
}
