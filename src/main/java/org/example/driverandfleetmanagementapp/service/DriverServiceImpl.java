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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DriverServiceImpl implements DriverService {


    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverMapper driverMapper;


    @Override
    @Transactional(readOnly = true)
    public Page<DriverDto> getAllDrivers(int page, int size) {
        log.info("Getting drivers with pagination");
        log.debug("Getting drivers with pagination - page: {}, size: {}, method=getAllDrivers", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Driver> driversPage = driverRepository.findAll(pageable);
        return driversPage.map(driverMapper::toDto);
    }



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'driver:' + #id")
    public DriverDto getDriverById(Integer id) {
        log.info("Getting driver by id");
        log.debug("Getting driver by ID: {}, method=getDriverById", id);

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));
        return driverMapper.toDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'license:' + #licenseNumber")
    public DriverDto getDriverByLicenseNumber(String licenseNumber) {
        log.info("Getting driver by license number");
        log.debug("Getting driver by license number: {}, method=getDriverByLicenseNumber", licenseNumber);
        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with license number " + licenseNumber + " not found"));
        return driverMapper.toDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, int page, int size) {
        log.info("Getting drivers by status with pagination");
        log.debug("Getting drivers by status: {} - page: {}, size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return driverRepository.findByStatus(status, pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'name:' + #firstName + ':' + #lastName + ':page' + #page + ':size' + #size")
    public Page<DriverDto> getDriversByName(String firstName, String lastName, int page, int size) {
        log.info("Getting drivers by name with pagination");
        log.debug("Getting drivers by name: {} {}, page: {}, size: {}", firstName, lastName, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return driverRepository.findByFirstNameAndLastName(firstName, lastName, pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'licenseType:' + #licenseType + ':page' + #page + ':size' + #size")
    public Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, int page, int size) {
        log.info("Getting drivers by license type with pagination");
        log.debug("Getting drivers by license type: {}, page: {}, size: {}", licenseType, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return driverRepository.findByLicenseType(licenseType, pageable).map(driverMapper::toDto);
    }




    @Override
    public DriverDto createDriver(DriverDto driverDto) {
        log.info("Creating new driver");
        log.debug("Creating new driver: {}, method=createDriver", driverDto);
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
        log.info("Updating driver with ID");
        log.debug("Updating driver with ID: {}, method=updateDriver", id);
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
        log.info("Updating status for driver");
        log.debug("Updating status for driver with ID: {} to {}, method=updateDriverStatus", id, status);
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
        log.info("Deleting driver with ID");
        log.debug("Deleting driver with ID: {}, method=deleteDriver", id);

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
        log.info("Assigning vehicle to a driver by their id");
        log.debug("Assigning vehicle ID: {} to driver ID: {}, method=assignVehicleToDriver", vehicleId, driverId);
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
        log.info("Removing vehicle from driver");
        log.debug("Removing vehicle ID: {} from driver ID: {}, method=removeVehicleFromDriver", vehicleId, driverId);
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
