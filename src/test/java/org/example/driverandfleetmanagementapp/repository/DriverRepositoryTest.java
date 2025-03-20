package org.example.driverandfleetmanagementapp.repository;


import org.example.driverandfleetmanagementapp.model.Driver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Test
    void findByLicenseNumber_shouldReturnDriver_whenLicenseNumberExists() {

        String existingLicenseNumber = "123456789";

        Optional<Driver> foundDriver = driverRepository.findByLicenseNumber(existingLicenseNumber);


        assertThat(foundDriver).isPresent();
        assertThat(foundDriver.get().getFirstName()).isEqualTo("Jan");
        assertThat(foundDriver.get().getLastName()).isEqualTo("Kowalski");
    }

    @Test
    void findByLicenseNumber_shouldReturnEmpty_whenLicenseNumberDoesNotExist() {

        String nonExistingLicenseNumber = "000000000";

        Optional<Driver> foundDriver = driverRepository.findByLicenseNumber(nonExistingLicenseNumber);

        assertThat(foundDriver).isEmpty();
    }

    @Test
    void findByStatus_shouldReturnDrivers_withMatchingStatus() {

        List<Driver> activeDrivers = driverRepository.findByStatus(Driver.DriverStatus.ACTIVE);
        List<Driver> onLeaveDrivers = driverRepository.findByStatus(Driver.DriverStatus.ON_LEAVE);
        List<Driver> inactiveDrivers = driverRepository.findByStatus(Driver.DriverStatus.INACTIVE);


        assertThat(activeDrivers).isNotEmpty();
        assertThat(activeDrivers).anyMatch(driver -> driver.getFirstName().equals("Jan"));

        assertThat(onLeaveDrivers).isNotEmpty();
        assertThat(onLeaveDrivers).anyMatch(driver -> driver.getFirstName().equals("Piotr"));

        assertThat(inactiveDrivers).isNotEmpty();
        assertThat(inactiveDrivers).anyMatch(driver -> driver.getFirstName().equals("Tomasz"));
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnDrivers_withMatchingNames() {

        List<Driver> janKowalski = driverRepository.findByFirstNameAndLastName("Jan", "Kowalski");
        List<Driver> annaNowak = driverRepository.findByFirstNameAndLastName("Anna", "Nowak");
        List<Driver> nonExistingDrivers = driverRepository.findByFirstNameAndLastName("Not existing", "Driver");


        assertThat(janKowalski).hasSize(1);
        assertThat(janKowalski.getFirst().getLicenseNumber()).isEqualTo("123456789");

        assertThat(annaNowak).hasSize(1);
        assertThat(annaNowak.getFirst().getLicenseNumber()).isEqualTo("987654321");

        assertThat(nonExistingDrivers).isEmpty();
    }

    @Test
    void findByLicenseType_shouldReturnDrivers_withMatchingLicenseType() {

        List<Driver> typeBDrivers = driverRepository.findByLicenseType(Driver.LicenseType.B);
        List<Driver> typeCDrivers = driverRepository.findByLicenseType(Driver.LicenseType.C);
        List<Driver> typeDDrivers = driverRepository.findByLicenseType(Driver.LicenseType.D);


        assertThat(typeBDrivers).isNotEmpty();
        assertThat(typeBDrivers).anyMatch(driver -> driver.getFirstName().equals("Jan"));

        assertThat(typeCDrivers).isNotEmpty();
        assertThat(typeCDrivers).anyMatch(driver -> driver.getFirstName().equals("Anna"));

        assertThat(typeDDrivers).isNotEmpty();
        assertThat(typeDDrivers).anyMatch(driver -> driver.getFirstName().equals("Piotr"));
    }
}