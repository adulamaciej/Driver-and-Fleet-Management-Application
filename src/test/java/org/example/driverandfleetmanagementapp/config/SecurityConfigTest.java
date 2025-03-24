package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void swaggerEndpoints_ShouldBeAccessibleForAll() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void apiEndpoints_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEndpoints_WithUserRole_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void modifyEndpoints_WithUserRole_ShouldBeForbidden() throws Exception {
        String driverJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"licenseNumber\":\"123456789\",\"licenseType\":\"B\",\"dateOfBirth\":\"1990-01-01\",\"phoneNumber\":\"123456789\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}";

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/drivers/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allEndpoints_WithAdminRole_ShouldBeAccessible() throws Exception {
        String driverJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"licenseNumber\":\"123456789\",\"licenseType\":\"B\",\"dateOfBirth\":\"1990-01-01\",\"phoneNumber\":\"123456789\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}";

        // GET requests
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());

        // POST requests
        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isCreated());

        // PUT requests should be accessible (will return 404 if resource doesn't exist)
        mockMvc.perform(put("/api/drivers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isNotFound());

        // PATCH requests should be accessible
        mockMvc.perform(patch("/api/drivers/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isNotFound());

        // DELETE requests should be accessible
        mockMvc.perform(delete("/api/drivers/999"))
                .andExpect(status().isNotFound());
    }
}