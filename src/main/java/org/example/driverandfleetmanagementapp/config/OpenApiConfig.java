package org.example.driverandfleetmanagementapp.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI fleetManagementApiConfig() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Fleet and Driver Management API")
                            .description("API for managing vehicles and drivers in a fleet management system. " +
                                    "This API requires authentication for most endpoints. " +
                                    "Use Basic Auth with provided user credentials.")
                            .contact(new Contact()
                                    .name("Fleet Management Team")
                                    .email("support@fleetmanagement.org"))
                            .license(new License()
                                    .name("MIT License")
                                    .url("https://opensource.org/licenses/MIT")))
                    .components(new Components()
                            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                                    .description("Enter JWT token obtained from /api/auth/login endpoint (access below). ")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }
    }