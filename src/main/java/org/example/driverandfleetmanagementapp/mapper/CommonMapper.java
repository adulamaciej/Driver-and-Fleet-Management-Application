package org.example.driverandfleetmanagementapp.mapper;

import org.example.driverandfleetmanagementapp.dto.DriverBasicDto;
import org.example.driverandfleetmanagementapp.dto.VehicleBasicDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    //They are being used by MapStruct automatically for creating lightweight object representation

    DriverBasicDto toDriverBasicDto(Driver driver);
    VehicleBasicDto toVehicleBasicDto(Vehicle vehicle);
}
