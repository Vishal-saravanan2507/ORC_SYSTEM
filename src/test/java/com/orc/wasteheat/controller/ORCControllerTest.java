package com.orc.wasteheat.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end check that the /api/orc/calculate endpoint is wired correctly
 * and returns real, backend-computed values (not hard-coded numbers) using
 * an in-memory H2 database (see src/test/resources/application.properties).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ORCControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String VALID_PAYLOAD = """
            {
              "flowRate": 10,
              "inletTemperature": 450,
              "outletTemperature": 200,
              "specificHeat": 1.1,
              "heatRecoveryFactor": 70,
              "orcEfficiency": 18,
              "operatingHours": 24,
              "operatingDays": 30,
              "gridEmissionFactor": 0.7
            }
            """;

    @Test
    void calculateEndpointReturnsCorrectValues() throws Exception {
        mockMvc.perform(post("/api/orc/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recoverableHeatKw").value(1925.0))
                .andExpect(jsonPath("$.electricalPowerKw").value(346.5))
                .andExpect(jsonPath("$.dailyEnergyKwh").value(8316.0))
                .andExpect(jsonPath("$.monthlyEnergyKwh").value(249480.0));
    }

    @Test
    void calculateEndpointRejectsInvalidTemperatures() throws Exception {
        String invalidPayload = """
                {
                  "flowRate": 10,
                  "inletTemperature": 100,
                  "outletTemperature": 200,
                  "specificHeat": 1.1,
                  "heatRecoveryFactor": 70,
                  "orcEfficiency": 18,
                  "operatingHours": 24,
                  "operatingDays": 30,
                  "gridEmissionFactor": 0.7
                }
                """;
        mockMvc.perform(post("/api/orc/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
