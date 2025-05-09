package org.example.driverandfleetmanagementapp.mapper;


import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.mapstruct.*;
import java.util.Set;


@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface VehicleMapper {

    @Mapping(target = "driver", source = "driver")
    VehicleDto toDto(Vehicle vehicle);

    @Mapping(target = "driver", ignore = true)
    Vehicle toEntity(VehicleDto vehicleDTO);

    Set<VehicleDto> toDtoSet(Set<Vehicle> vehicles);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "driver", ignore = true)
    void updateVehicleFromDto(VehicleDto vehicleDTO, @MappingTarget Vehicle vehicle);
}