package org.example.driverandfleetmanagementapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;


@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) // Enables easy, automatic pagination support.
public class DriverAndFleetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverAndFleetManagementApplication.class, args);
    }

}
