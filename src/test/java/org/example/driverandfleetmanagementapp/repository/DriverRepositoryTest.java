package org.example.driverandfleetmanagementapp.repository;


import org.example.driverandfleetmanagementapp.model.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    private Driver.DriverBuilder defaultDriverBuilder;

    @BeforeEach
    void setUp() {
        defaultDriverBuilder = Driver.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .licenseNumber("543832578")
                .licenseType(Driver.LicenseType.B)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("123456789")
                .email("jankowalski@example.com")
                .status(Driver.DriverStatus.ACTIVE);


    }

    @Test
    void findByLicenseNumber_ShouldReturnDriver() {
        Driver driver = defaultDriverBuilder.build();
        driverRepository.save(driver);

        String savedLicenseNumber = driver.getLicenseNumber();

        Optional<Driver> driverOptional = driverRepository.findByLicenseNumber(savedLicenseNumber);

        assertThat(driverOptional).isPresent();
        assertThat(driverOptional.get().getLicenseNumber()).isEqualTo("543832578");
    }

    @Test
    void findById_ShouldReturnDriversId() {
        Driver driver = defaultDriverBuilder.build();
        driverRepository.save(driver);

        Integer savedId = driver.getId();

        Optional<Driver> driverOptional = driverRepository.findById(savedId);

        assertThat(driverOptional).isPresent();
        assertThat(driverOptional.get().getId()).isEqualTo(savedId);
    }

    @Test
    void findByStatus_ShouldReturnDriversWithStatus() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> drivers = driverRepository.findByStatus(Driver.DriverStatus.ACTIVE, pageable);

        assertThat(drivers.getContent()).isNotEmpty();
        assertThat(drivers.getContent().getFirst().getStatus()).isEqualTo(Driver.DriverStatus.ACTIVE);
    }

    @Test
    void findByFirstNameAndLastName_ShouldReturnDrivers() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> drivers = driverRepository.findByFirstNameAndLastName("Jan", "Kowalski", pageable);

        assertThat(drivers.getContent()).isNotEmpty();
        assertThat(drivers.getContent().getFirst().getFirstName()).isEqualTo("Jan");
        assertThat(drivers.getContent().getFirst().getLastName()).isEqualTo("Kowalski");
    }

    @Test
    void findByLicenseType_ShouldReturnDrivers() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> drivers = driverRepository.findByLicenseType(Driver.LicenseType.B, pageable);

        assertThat(drivers.getContent()).isNotEmpty();
        assertThat(drivers.getContent().getFirst().getLicenseType()).isEqualTo(Driver.LicenseType.B);
    }

    @Test
    void getAllDrivers_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Driver> allDrivers = driverRepository.findAll(pageable);

        assertThat(allDrivers).isNotNull();
        assertThat(allDrivers.getContent()).isNotEmpty();
        assertThat(allDrivers.getContent().size()).isLessThanOrEqualTo(10);
    }
}
