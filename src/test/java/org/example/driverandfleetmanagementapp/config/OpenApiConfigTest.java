package org.example.driverandfleetmanagementapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();
    private final OpenAPI openAPI = openApiConfig.fleetManagementApiConfig();

    @Test
    void openApiInfoShouldBeConfiguredCorrectly() {
        Info info = openAPI.getInfo();

        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Fleet and Driver Management API");
        assertThat(info.getDescription()).isEqualTo("API for managing vehicles and drivers in a fleet management system. " +
                "This API requires authentication for most endpoints. " +
                "Use Basic Auth with provided user credentials.");
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

    @Test
    void securitySchemeShouldBeConfiguredCorrectly() {
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("basicAuth");

        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("basic");
        assertThat(securityScheme.getDescription()).isEqualTo("Use your username and password to access secured endpoints.");
    }

    @Test
    void securityRequirementShouldBeConfiguredCorrectly() {
        assertThat(openAPI.getSecurity()).isNotNull();
        assertThat(openAPI.getSecurity()).hasSize(1);
        assertThat(openAPI.getSecurity().getFirst().get("basicAuth")).isNotNull();
    }
}