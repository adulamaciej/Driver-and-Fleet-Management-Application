package org.example.driverandfleetmanagementapp.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private VehicleDto validVehicle;


    @BeforeEach
    void setUp() {
        validVehicle = VehicleDto.builder()
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.now())
                .technicalInspectionDate(LocalDate.now().plusYears(1))
                .productionYear(2020)
                .mileage(0.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }

    @Test
    void userDetailsService_shouldContainAdminAndUserAccounts() {
        UserDetailsService userDetailsService = new SecurityConfig().userDetailsService(passwordEncoder);

        assertEquals("admin", userDetailsService.loadUserByUsername("admin").getUsername());
        assertEquals("user", userDetailsService.loadUserByUsername("user").getUsername());
    }

    @Test
    void unauthenticated_shouldBeDeniedAccess() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swagger_shouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userRole_canAccessGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userRole_cannotAccessModificationEndpoints() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVehicle)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRole_canAccessGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRole_canAccessModificationEndpoints() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validVehicle)))
                .andExpect(status().is2xxSuccessful());
    }


}