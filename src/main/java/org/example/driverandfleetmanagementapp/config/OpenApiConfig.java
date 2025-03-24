package org.example.driverandfleetmanagementapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiConfig {

    @Bean
    public OpenAPI fleetManagementApiConfig() {
        log.debug("Configuring OpenAPI");
        return new OpenAPI()
                .info(new Info()
                        .title("Fleet and Driver Management API")
                        .description("API for managing vehicles and drivers in a fleet management system")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fleet Management Team")
                                .email("support@fleetmanagement.org"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}