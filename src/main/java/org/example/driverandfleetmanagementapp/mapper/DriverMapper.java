package org.example.driverandfleetmanagementapp.mapper;

import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface DriverMapper {

    @Mapping(target = "vehicles", source = "vehicles")
    DriverDto toDto(Driver driver);

    @Mapping(target = "vehicles", ignore = true)
    Driver toEntity(DriverDto driverDto);

    List<DriverDto> toDtoList(List<Driver> drivers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "vehicles", ignore = true)
    void updateDriverFromDto(DriverDto driverDTO, @MappingTarget Driver driver);
}