package org.example.driverandfleetmanagementapp.service;


import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.utilis.LicenseValidator;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.*;
import org.example.driverandfleetmanagementapp.mapper.DriverMapper;
import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.example.driverandfleetmanagementapp.repository.DriverRepository;
import org.example.driverandfleetmanagementapp.repository.VehicleRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;


@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {


    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverMapper driverMapper;


    @Override
    @Transactional(readOnly = true)
    public Page<DriverDto> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'driver:' + #id")
    public DriverDto getDriverById(Integer id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));
        return driverMapper.toDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'license:' + #licenseNumber")
    public DriverDto getDriverByLicenseNumber(String licenseNumber) {
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with license number " + licenseNumber + " not found"));
        return driverMapper.toDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDto> getDriversByStatus(Driver.DriverStatus status,Pageable pageable) {
        return driverRepository.findByStatus(status, pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'name:' + #firstName + ':' + #lastName + ':page' + #pageable.pageNumber + ':size' + #pageable.pageSize")
    public Page<DriverDto> getDriversByName(String firstName, String lastName, Pageable pageable) {
        return driverRepository.findByFirstNameAndLastName(firstName, lastName, pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'licenseType:' + #licenseType + ':page' + #pageable.pageNumber + ':size' + #pageable.pageSize")
    public Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, Pageable pageable) {
        return driverRepository.findByLicenseType(licenseType, pageable).map(driverMapper::toDto);
    }


    @Override
    public DriverDto createDriver(DriverDto driverDto) {
        if (driverRepository.findByLicenseNumber(driverDto.getLicenseNumber()).isPresent()) {
            throw new ResourceConflictException("Driver with license number " + driverDto.getLicenseNumber() + " already exists");
        }
        Driver driver = driverMapper.toEntity(driverDto);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #id"),
            @CacheEvict(value = "drivers", key = "'license:' + #driverDto.licenseNumber"),
            @CacheEvict(value = "drivers", key = "'name:' + #driverDto.firstName + ':' + #driverDto.lastName"),
            @CacheEvict(value = "drivers", key = "'licenseType:' + #driverDto.licenseType")
    })
    public DriverDto updateDriver(Integer id, DriverDto driverDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));
        driverRepository.findByLicenseNumber(driverDto.getLicenseNumber())
                .filter(d -> !d.getId().equals(id))
                .ifPresent(d -> {
                    throw new ResourceConflictException("License number " + driverDto.getLicenseNumber() + " already in use");
                });
        driverMapper.updateDriverFromDto(driverDto, driver);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #id"),
            @CacheEvict(value = "drivers", key = "'name:' + #result.firstName + ':' + #result.lastName"),
            @CacheEvict(value = "drivers", key = "'license:' + #result.licenseNumber"),
            @CacheEvict(value = "drivers", key = "'licenseType:' + #result.licenseType")
    })
    public DriverDto updateDriverStatus(Integer id, Driver.DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));

        if (status == Driver.DriverStatus.SUSPENDED && !driver.getVehicles().isEmpty()) {
            throw new BusinessLogicException("Cannot suspend driver with assigned vehicles. Please remove vehicle assignments first.");
        }
        driver.setStatus(status);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    @CacheEvict(value = "drivers", key = "'driver:' + #id")
    public void deleteDriver(Integer id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));

        if (vehicleRepository.existsByDriverId(id)) {
            throw new BusinessLogicException("Cannot delete driver with assigned vehicles");
        }

        driverRepository.delete(driver);
    }
    @Override
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #driverId"),
            @CacheEvict(value = "vehicles", key = "'vehicle:' + #vehicleId"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #driverId")
    })
    public DriverDto assignVehicleToDriver(Integer driverId, Integer vehicleId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + driverId + " not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + vehicleId + " not found"));

        if (vehicle.getStatus() == Vehicle.VehicleStatus.OUT_OF_ORDER) {
            throw new BusinessLogicException("Cannot assign vehicle with status OUT_OF_ORDER to a driver");
        }

        if (driver.getStatus() == Driver.DriverStatus.SUSPENDED) {
            throw new BusinessLogicException("Cannot assign vehicle to suspended driver");
        }

        if (vehicle.getDriver() != null) {
            throw new ResourceConflictException("Vehicle is already assigned to a driver");
        }

        if (driver.getVehicles().size() >= 2){
            throw new BusinessLogicException("You cannot have more than 2 vehicles assigned");
        }

        if (!LicenseValidator.canDriverOperateVehicle(driver.getLicenseType(), vehicle.getType())) {
            throw new BusinessLogicException("Driver's license type " + driver.getLicenseType() +
                    " does not allow operating vehicle of type " + vehicle.getType());
        }

        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);
        driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #driverId"),
            @CacheEvict(value = "vehicles", key = "'vehicle:' + #vehicleId"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #driverId")
    })
    public DriverDto removeVehicleFromDriver(Integer driverId, Integer vehicleId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + driverId + " not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + vehicleId + " not found"));
        if (vehicle.getDriver() == null || !vehicle.getDriver().getId().equals(driverId)) {
            throw new BusinessLogicException("Vehicle is not assigned to this driver");
        }
        vehicle.setDriver(null);
        driver.getVehicles().remove(vehicle);
        driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }


}
