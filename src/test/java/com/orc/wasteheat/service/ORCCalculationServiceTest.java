package com.orc.wasteheat.service;

import com.orc.wasteheat.dto.ORCCalculationRequest;
import com.orc.wasteheat.dto.ORCCalculationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the ORC calculation engine against the manually worked example
 * from the project specification:
 *   m = 10 kg/s, Cp = 1.1 kJ/kg.K, Tin = 450C, Tout = 200C
 *   Q = 2750 kW -> Recoverable Heat (70%) = 1925 kW
 *   Electrical Power (18%) = 346.5 kW
 *   Daily Energy (24h) = 8316 kWh, Monthly Energy (30d) = 249480 kWh
 */
class ORCCalculationServiceTest {

    private ORCCalculationService service;
    private ORCCalculationRequest baseRequest;

    @BeforeEach
    void setUp() {
        service = new ORCCalculationService();
        baseRequest = new ORCCalculationRequest();
        baseRequest.setFlowRate(10.0);
        baseRequest.setInletTemperature(450.0);
        baseRequest.setOutletTemperature(200.0);
        baseRequest.setSpecificHeat(1.1);
        baseRequest.setHeatRecoveryFactor(70.0);
        baseRequest.setOrcEfficiency(18.0);
        baseRequest.setOperatingHours(24);
        baseRequest.setOperatingDays(30);
        baseRequest.setGridEmissionFactor(0.7);
    }

    @Test
    void shouldCalculateRecoverableHeatCorrectly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        assertEquals(1925.0, result.getRecoverableHeatKw(), 0.001);
    }

    @Test
    void shouldCalculateOrcElectricalPowerCorrectly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        assertEquals(346.5, result.getElectricalPowerKw(), 0.001);
    }

    @Test
    void shouldCalculateDailyEnergyCorrectly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        assertEquals(8316.0, result.getDailyEnergyKwh(), 0.001);
    }

    @Test
    void shouldCalculateMonthlyEnergyCorrectly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        assertEquals(249480.0, result.getMonthlyEnergyKwh(), 0.001);
    }

    @Test
    void shouldCalculateAnnualEnergyAsTwelveTimesMonthly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        assertEquals(result.getMonthlyEnergyKwh() * 12.0, result.getAnnualEnergyKwh(), 0.001);
    }

    @Test
    void shouldCalculateCO2ReductionCorrectly() {
        ORCCalculationResponse result = service.calculate(baseRequest);
        // dailyCO2 = dailyEnergy * emissionFactor = 8316 * 0.7
        assertEquals(8316.0 * 0.7, result.getDailyCO2ReductionKg(), 0.01);
        // monthlyCO2 (tonnes) = monthlyEnergy * emissionFactor / 1000
        assertEquals((249480.0 * 0.7) / 1000.0, result.getMonthlyCO2ReductionTonnes(), 0.01);
    }

    @Test
    void shouldRejectInvalidTemperatureWhereInletNotGreaterThanOutlet() {
        baseRequest.setInletTemperature(150.0);
        baseRequest.setOutletTemperature(200.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.calculate(baseRequest));
        assertTrue(ex.getMessage().toLowerCase().contains("inlet temperature"));
    }

    @Test
    void shouldRejectInvalidEfficiencyAboveOneHundredPercent() {
        baseRequest.setOrcEfficiency(150.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.calculate(baseRequest));
        assertTrue(ex.getMessage().toLowerCase().contains("orc efficiency"));
    }

    @Test
    void shouldRejectZeroOrNegativeFlowRate() {
        baseRequest.setFlowRate(0.0);
        assertThrows(IllegalArgumentException.class, () -> service.calculate(baseRequest));
    }

    @Test
    void shouldRejectHeatRecoveryFactorOutOfRange() {
        baseRequest.setHeatRecoveryFactor(-5.0);
        assertThrows(IllegalArgumentException.class, () -> service.calculate(baseRequest));
    }
}
