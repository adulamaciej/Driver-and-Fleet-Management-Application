package org.example.driverandfleetmanagementapp.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void openApiInfoShouldBeConfiguredCorrectly() {
        Info info = openAPI.getInfo();

        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Fleet and Driver Management API");
        assertThat(info.getDescription()).isEqualTo("API for managing vehicles and drivers in a fleet management system");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void openApiContactInfoShouldBeConfiguredCorrectly() {
        Contact contact = openAPI.getInfo().getContact();

        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Fleet Management Team");
        assertThat(contact.getEmail()).isEqualTo("support@fleetmanagement.org");
    }

    @Test
    void openApiLicenseInfoShouldBeConfiguredCorrectly() {
        License license = openAPI.getInfo().getLicense();

        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }
}
