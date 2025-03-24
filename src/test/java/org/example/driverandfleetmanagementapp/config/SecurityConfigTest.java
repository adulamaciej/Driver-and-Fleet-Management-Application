package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void userDetailsService_returnsValidUserDetailsService() {
        SecurityConfig securityConfig = new SecurityConfig();
        assert securityConfig.userDetailsService() != null;
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
        String validVehicleJson = createValidVehicleJson();

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
        String validVehicleJson = createValidVehicleJson();

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validVehicleJson))
                .andExpect(status().is4xxClientError());
    }

    // JSON  representing VehicleDto object with all required field
    private String createValidVehicleJson() {
        return "{"
                + "\"licensePlate\": \"ABC123\","
                + "\"brand\": \"Toyota\","
                + "\"model\": \"Corolla\","
                + "\"productionYear\": 2020,"
                + "\"type\": \"CAR\","
                + "\"registrationDate\": \"2020-01-01\","
                + "\"technicalInspectionDate\": \"2025-01-01\","
                + "\"mileage\": 10000,"
                + "\"status\": \"AVAILABLE\""
                + "}";
    }
}