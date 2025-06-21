package org.example.driverandfleetmanagementapp.service.vehicle;


import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.audit.Auditable;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
import org.example.driverandfleetmanagementapp.mapper.VehicleMapper;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;



@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleMapper vehicleMapper;


    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDto> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "'vehicle:' + #id")
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " not found"));
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "'licensePlate:' + #licensePlate")
    public VehicleDto getVehicleByLicensePlate(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with license plate " + licensePlate + " not found"));
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleDto> getVehiclesByStatus(Vehicle.VehicleStatus status, Pageable pageable) {
        return vehicleRepository.findByStatus(status, pageable).map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "'brandAndModel:' + #brand + ':' + #model")
    public Page<VehicleDto> getVehiclesByBrandAndModel(String brand, String model, Pageable pageable) {
        return vehicleRepository.findByBrandAndModel(brand, model, pageable).map(vehicleMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "'type:' + #type")
    public Page<VehicleDto> getVehiclesByType(Vehicle.VehicleType type, Pageable pageable) {
        return vehicleRepository.findByType(type, pageable).map(vehicleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vehicles", key = "'driver:' + #driverId")
    public Set<VehicleDto> getVehiclesByDriverId(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver with ID " + driverId + " not found");
        }
        Set<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        return vehicleMapper.toDtoSet(vehicles);
    }


    @Override
    @Auditable(entity = "VEHICLE", action = "CREATE")
    public VehicleDto createVehicle(VehicleDto vehicleDto) {

        if (vehicleRepository.findByLicensePlate(vehicleDto.getLicensePlate()).isPresent()) {
            throw new ResourceConflictException("Vehicle with license plate " + vehicleDto.getLicensePlate() + " already exists");
        }

        if (vehicleDto.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new BusinessLogicException("New vehicle must be created with status AVAILABLE. Current status: " + vehicleDto.getStatus());
        }

        validateTechnicalInspectionDate(vehicleDto);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        vehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    @Auditable(entity = "VEHICLE", action = "UPDATE")
    @Caching(evict = {
            @CacheEvict(value = "vehicles", key = "'vehicle:' + #id"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #vehicleDto.driver.id")
    })
    public VehicleDto updateVehicle(Long id, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " not found"));

        vehicleRepository.findByLicensePlate(vehicleDto.getLicensePlate())
                .filter(v -> !v.getId().equals(id))
                .ifPresent(d -> {
                    throw new ResourceConflictException("License plate " + vehicleDto.getLicensePlate() +
                            " already in use by vehicle with ID: " + d.getId());
                });
        validateTechnicalInspectionDate(vehicleDto);
        vehicleMapper.updateVehicleFromDto(vehicleDto, vehicle);
        return vehicleMapper.toDto(vehicle);
    }

    @Override
    @Auditable(entity = "VEHICLE", action = "DELETE")
    @Caching(evict = {
            @CacheEvict(value = "vehicles", allEntries = true),
            @CacheEvict(value = "drivers", allEntries = true)
    })
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " not found"));

        if (vehicle.getDriver() != null) {
            throw new BusinessLogicException("Cannot delete vehicle with ID " + id +
                    " as it is assigned to driver ID: " + vehicle.getDriver().getId() +
                    ". Please remove the assignment first.");
        }

        if (vehicle.getStatus() == Vehicle.VehicleStatus.IN_USE ||
                vehicle.getStatus() == Vehicle.VehicleStatus.IN_SERVICE) {
            throw new BusinessLogicException("Cannot delete vehicle with ID " + id +
                    " as it is currently " + vehicle.getStatus());
        }
        vehicleRepository.delete(vehicle);
    }


    @Override
    @Auditable(entity = "VEHICLE", action = "MILEAGE_UPDATE")
    @CacheEvict(value = "vehicles", key = "'vehicle:' + #id")
    public VehicleDto updateVehicleMileage(Long id, Double mileage) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " not found"));

        if (mileage < 0) {
            throw new BusinessLogicException("Mileage cannot be negative");
        }

        Double currentMileage = vehicle.getMileage();
        if (mileage < currentMileage) {
            throw new BusinessLogicException("New mileage (" + mileage + ") cannot be less than current mileage (" + currentMileage + ")");
        }

        vehicle.setMileage(mileage);
        return vehicleMapper.toDto(vehicle);
    }


    @Override
    @Auditable(entity = "VEHICLE", action = "STATUS_CHANGE")
    @CacheEvict(value = "vehicles", key = "'vehicle:' + #id")
    public VehicleDto updateVehicleStatus(Long id, Vehicle.VehicleStatus status) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " not found"));

        if (status == Vehicle.VehicleStatus.OUT_OF_ORDER && vehicle.getDriver() != null) {
            throw new BusinessLogicException("Cannot change vehicle status to OUT_OF_ORDER while driver is assigned. " +
                    "Please remove driver assignment first.");
        }

        if (status == Vehicle.VehicleStatus.IN_SERVICE && vehicle.getDriver() != null) {
            throw new BusinessLogicException("Cannot change vehicle status to IN_SERVICE while driver is assigned. " +
                    "Please remove driver assignment first. Driver can be reassigned to another available vehicle.");
        }

        if (status == Vehicle.VehicleStatus.IN_USE && vehicle.getDriver() == null) {
            throw new BusinessLogicException("Cannot change vehicle status to IN_USE without an assigned driver. " +
                    "Please assign a driver first.");
        }

        if (vehicle.getStatus() == Vehicle.VehicleStatus.OUT_OF_ORDER && status == Vehicle.VehicleStatus.AVAILABLE) {
            throw new BusinessLogicException("Cannot change vehicle status directly from OUT_OF_ORDER to AVAILABLE. " +
                    "Vehicle must go through service first.");
        }

        vehicle.setStatus(status);
        return vehicleMapper.toDto(vehicle);
    }



    private void validateTechnicalInspectionDate(VehicleDto vehicleDto) {
        LocalDate now = LocalDate.now();
        LocalDate inspectionDate = vehicleDto.getTechnicalInspectionDate();

        if (inspectionDate.isBefore(now)) {
            throw new BusinessLogicException("Technical inspection has expired for vehicle with license plate "
                    + vehicleDto.getLicensePlate() + ". Inspection date: " + inspectionDate);
        }
    }
}