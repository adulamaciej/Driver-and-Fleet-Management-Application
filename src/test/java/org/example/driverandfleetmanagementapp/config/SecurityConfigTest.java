package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;



@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void passwordEncoderShouldBeBCrypt() {
        assertThat(passwordEncoder).isNotNull();
        String encoded = passwordEncoder.encode("test");
        assertThat(encoded).startsWith("$2a$");
        assertThat(passwordEncoder.matches("test", encoded)).isTrue();
    }

    @Test
    void apiEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void apiEndpointsShouldBeAccessibleForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpointsShouldBeDeniedForRegularUsers() throws Exception {
        String driverJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"licenseNumber\":\"837591246\",\"licenseType\":\"B\",\"dateOfBirth\":\"1990-11-01\",\"phoneNumber\":\"123496789\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}";

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpointsShouldBeAccessibleForAdmins() throws Exception {
        String driverJson = "{\"firstName\":\"Test\",\"lastName\":\"User\",\"licenseNumber\":\"987612345\",\"licenseType\":\"B\",\"dateOfBirth\":\"1990-01-01\",\"phoneNumber\":\"123456789\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}";

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void regularEndpointsShouldBeAccessibleForAdmins() throws Exception {
        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk());
    }
}