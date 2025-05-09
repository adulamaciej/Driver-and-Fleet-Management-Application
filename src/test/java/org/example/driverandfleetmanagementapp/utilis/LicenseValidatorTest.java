package org.example.driverandfleetmanagementapp.utilis;


import org.example.driverandfleetmanagementapp.model.Driver;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;


@ActiveProfiles("test")
public class LicenseValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "B, CAR, true",
            "C, CAR, true",
            "C, VAN, true",
            "C, TRUCK, true",
            "D, CAR, true",
            "D, BUS, true",
            "CE, CAR, true",
            "CE, VAN, true",
            "CE, TRUCK, true",
            "DE, CAR, true",
            "DE, BUS, true",
            "B, VAN, false",
            "B, TRUCK, false",
            "B, BUS, false",
            "C, BUS, false",
            "D, VAN, false",
            "D, TRUCK, false"
    })
    void testLicenseVehicleCompatibility(String licenseType, String vehicleType, boolean expected) {

        boolean result = LicenseValidator.canDriverOperateVehicle(
                Driver.LicenseType.valueOf(licenseType),
                Vehicle.VehicleType.valueOf(vehicleType));


        assertThat(result).isEqualTo(expected);
    }
    @Test
    void testAllLicenseAndVehicleCombinations() {
        for (Driver.LicenseType licenseType : Driver.LicenseType.values()) {
            for (Vehicle.VehicleType vehicleType : Vehicle.VehicleType.values()) {
                assertThatNoException()
                        .isThrownBy(() -> LicenseValidator.canDriverOperateVehicle(licenseType, vehicleType));
            }
        }
    }
}