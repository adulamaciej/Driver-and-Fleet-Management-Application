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
    private Driver testDriver;

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

        testDriver = defaultDriverBuilder.build();
        driverRepository.save(testDriver);

    }

    @Test
    void findByLicenseNumber_ShouldReturnDriver() {
        Optional<Driver> driverOptional = driverRepository.findByLicenseNumber(testDriver.getLicenseNumber());

        assertThat(driverOptional).isPresent();
        assertThat(driverOptional.get().getLicenseNumber()).isEqualTo(testDriver.getLicenseNumber());
        assertThat(driverOptional.get()).isEqualTo(testDriver);
    }

    @Test
    void findById_ShouldReturnDriversId() {
        Optional<Driver> driverOptional = driverRepository.findById(testDriver.getId());


        assertThat(driverOptional).isPresent();
        assertThat(driverOptional.get().getId()).isEqualTo(testDriver.getId());
    }

    @Test
    void findByStatus_ShouldReturnDriversWithStatus() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<Driver> drivers = driverRepository.findByStatus(testDriver.getStatus(), pageable);

        assertThat(drivers.getContent()).isNotEmpty();
        assertThat(drivers.getContent()).contains(testDriver);
    }


    @Test
    void findByFirstNameAndLastName_ShouldReturnDrivers() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<Driver> drivers = driverRepository.findByFirstNameAndLastName(
                testDriver.getFirstName(), testDriver.getLastName(), pageable);

        assertThat(drivers).isNotEmpty();
        assertThat(drivers.getContent().getFirst().getFirstName()).isEqualTo(testDriver.getFirstName());
        assertThat(drivers.getContent().getFirst().getLastName()).isEqualTo(testDriver.getLastName());
        assertThat(drivers).contains(testDriver);
    }


    @Test
    void findByLicenseType_ShouldReturnDrivers() {
        Pageable pageable = PageRequest.of(0, 20);

        Page<Driver> drivers = driverRepository.findByLicenseType(testDriver.getLicenseType(), pageable);

        assertThat(drivers.getContent()).isNotEmpty();
        assertThat(drivers.getContent().getFirst().getLicenseType()).isEqualTo(Driver.LicenseType.B);
        assertThat(drivers).contains(testDriver);

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
