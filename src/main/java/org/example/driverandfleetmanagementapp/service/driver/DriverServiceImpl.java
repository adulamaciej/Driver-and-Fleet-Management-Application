package org.example.driverandfleetmanagementapp.service.driver;



import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.example.driverandfleetmanagementapp.audit.Auditable;
import org.example.driverandfleetmanagementapp.dto.DriverDto;
import org.example.driverandfleetmanagementapp.exception.custom.BusinessLogicException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceConflictException;
import org.example.driverandfleetmanagementapp.exception.custom.ResourceNotFoundException;
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
    @Cacheable(value = "drivers", key = "'driver:' + #id")
    public DriverDto getDriverById(Long id) {
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
    public Page<DriverDto> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverDto> getDriversByStatus(Driver.DriverStatus status, Pageable pageable) {
        return driverRepository.findByStatus(status, pageable).map(driverMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'vehicle:' + #vehicleId")
    public DriverDto getDriverByVehicleId(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + vehicleId + " not found"));

        if (vehicle.getDriver() == null) {
            throw new BusinessLogicException("Vehicle with ID " + vehicleId + " has no assigned driver");
        }
        return driverMapper.toDto(vehicle.getDriver());
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'name:' + #firstName + ':' + #lastName")
    public Page<DriverDto> getDriversByFirstAndLastName(String firstName, String lastName, Pageable pageable) {
        return driverRepository.findByFirstNameAndLastName(firstName, lastName, pageable)
                .map(driverMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "drivers", key = "'licenseType:' + #licenseType")
    public Page<DriverDto> getDriversByLicenseType(Driver.LicenseType licenseType, Pageable pageable) {
        return driverRepository.findByLicenseType(licenseType, pageable).map(driverMapper::toDto);
    }


    @Override
    @Timed("fleet.driver.creation.time")
    @Auditable(entity = "DRIVER", action = "CREATE")
    public DriverDto createDriver(DriverDto driverDto) {
        if (driverRepository.findByLicenseNumber(driverDto.getLicenseNumber()).isPresent()) {
            throw new ResourceConflictException("Driver with license number " + driverDto.getLicenseNumber() + " already exists");
        }

        if (driverDto.getStatus() != Driver.DriverStatus.ACTIVE){
            throw new BusinessLogicException("New drivers must be created with ACTIVE status");
        }

        Driver driver = driverMapper.toEntity(driverDto);
        driver = driverRepository.save(driver);
        return driverMapper.toDto(driver);
    }

    @Override
    @Timed("fleet.driver.update.time")
    @Auditable(entity = "DRIVER", action = "UPDATE")
    @Caching(evict = {
            @CacheEvict(value = "drivers", key = "'driver:' + #id"),
            @CacheEvict(value = "drivers", key = "'licenseType:' + #driverDto.licenseType"),
            @CacheEvict(value = "vehicles", key = "'driver:' + #id")
    })
    public DriverDto updateDriver(Long id, DriverDto driverDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));

        driverRepository.findByLicenseNumber(driverDto.getLicenseNumber())
                .filter(d -> !d.getId().equals(id))
                .ifPresent(d -> {
                    throw new ResourceConflictException("License number " + driverDto.getLicenseNumber() + " already in use" +d.getId());
                });

        driverMapper.updateDriverFromDto(driverDto, driver);


        return driverMapper.toDto(driver);
    }

    @Override
    @Auditable(entity = "DRIVER", action = "STATUS_CHANGE")
    @CacheEvict(value = "drivers", key = "'driver:' + #id")
    public DriverDto updateDriverStatus(Long id, Driver.DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));
        driver.setStatus(status);
        return driverMapper.toDto(driver);
    }


    @Override
    @Timed("fleet.driver.delete.time")
    @Auditable(entity = "DRIVER", action = "DELETE")
    @Caching(evict = {
            @CacheEvict(value = "drivers", allEntries = true),
            @CacheEvict(value = "vehicles", allEntries = true)
    })
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with ID " + id + " not found"));

        if (vehicleRepository.existsByDriverId(id)) {
            throw new BusinessLogicException("Cannot delete driver with assigned vehicles");
        }

        if (driver.getStatus() == Driver.DriverStatus.ACTIVE ||
                driver.getStatus() == Driver.DriverStatus.ON_LEAVE) {
            throw new BusinessLogicException("Cannot delete driver with status " + driver.getStatus() +
                    ". Driver must be INACTIVE or SUSPENDED before deletion.");
        }
        driverRepository.delete(driver);

    }

}
