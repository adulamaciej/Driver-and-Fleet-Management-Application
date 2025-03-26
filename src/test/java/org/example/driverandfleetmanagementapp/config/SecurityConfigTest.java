package org.example.driverandfleetmanagementapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driverandfleetmanagementapp.dto.VehicleDto;
import org.example.driverandfleetmanagementapp.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
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

    @Test
    void userDetailsService_shouldReturnValidUsers() {
        UserDetailsService userDetailsService = new SecurityConfig().userDetailsService(passwordEncoder);
        assertNotNull(userDetailsService.loadUserByUsername("admin"));
        assertNotNull(userDetailsService.loadUserByUsername("user"));
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
        String validVehicleJson = objectMapper.writeValueAsString(createValidVehicle());

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validVehicleJson))
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
        String validVehicleJson = objectMapper.writeValueAsString(createValidVehicle());

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validVehicleJson))
                .andExpect(status().is2xxSuccessful());
    }

    // Helping method for creating an object of a vehicle for tests
    private VehicleDto createValidVehicle() {
        return VehicleDto.builder()
                .licensePlate("ABC123")
                .brand("Toyota")
                .model("Corolla")
                .productionYear(2020)
                .type(Vehicle.VehicleType.CAR)
                .registrationDate(LocalDate.of(2020, 1, 1))
                .technicalInspectionDate(LocalDate.of(2026, 1, 1))
                .mileage(10000.0)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();
    }
}