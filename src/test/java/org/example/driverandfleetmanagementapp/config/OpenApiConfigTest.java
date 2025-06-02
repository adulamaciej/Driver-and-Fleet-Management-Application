package org.example.driverandfleetmanagementapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class OpenApiConfigTest {


    private  OpenAPI openAPI;

    @BeforeEach
    void setUp(){
        OpenApiConfig openApiConfig = new OpenApiConfig();
        openAPI = openApiConfig.fleetManagementApiConfig();
    }


    @Test
    void openApiInfoShouldBeConfiguredCorrectly() {
        Info info = openAPI.getInfo();

        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Fleet and Driver Management API");
    }

    @Test
    void openApiContactInfoShouldBeConfiguredCorrectly() {
        Contact contact = openAPI.getInfo().getContact();

        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Fleet Management Team");
        assertThat(contact.getEmail()).isEqualTo("support@fleetmanagement.org");
    }


    @Test
    void securitySchemeShouldBeConfiguredCorrectly() {
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");

        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }
}