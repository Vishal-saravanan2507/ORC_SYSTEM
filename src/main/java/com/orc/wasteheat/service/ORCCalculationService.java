package com.orc.wasteheat.service;

import com.orc.wasteheat.dto.ORCCalculationRequest;
import com.orc.wasteheat.dto.ORCCalculationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ORCCalculationService
 * ---------------------
 * This is the calculation engine for the whole application. It implements a
 * SIMPLIFIED, college-project-level engineering estimation model for waste
 * heat recovery via an Organic Rankine Cycle (ORC). It is intentionally kept
 * simple and readable rather than being a certified thermodynamic design tool.
 *
 * All figures produced here should be treated and displayed as
 * "Estimated" / "Simulation Result" values.
 */
@Service
public class ORCCalculationService {

    /**
     * Runs the full ORC estimation pipeline for a given set of operating
     * parameters and returns a structured result.
     */
    public ORCCalculationResponse calculate(ORCCalculationRequest req) {
        validate(req);

        // Step 1: Temperature difference across the waste gas stream
        double deltaT = req.getInletTemperature() - req.getOutletTemperature();

        // Step 2: Thermal energy available from the waste gas stream
        // Q (kW) = mass flow rate (kg/s) x specific heat (kJ/kg.K) x deltaT (K)
        // kg/s * kJ/kg.K * K = kJ/s = kW, so no extra conversion factor is needed.
        double totalHeatKw = req.getFlowRate() * req.getSpecificHeat() * deltaT;

        // Step 3: Apply the heat recovery factor (fraction of total heat that
        // can practically be captured by the heat-recovery/evaporator system).
        double recoverableHeatKw = totalHeatKw * (req.getHeatRecoveryFactor() / 100.0);

        // Step 4: Estimate electrical power output using the ORC thermal efficiency
        double electricalPowerKw = recoverableHeatKw * (req.getOrcEfficiency() / 100.0);

        // Step 5: Estimate daily / monthly / annual energy generation
        double dailyEnergyKwh = electricalPowerKw * req.getOperatingHours();
        double monthlyEnergyKwh = dailyEnergyKwh * req.getOperatingDays();
        double annualEnergyKwh = monthlyEnergyKwh * 12.0;

        // Step 6: Estimate avoided CO2 emissions based on the selected grid emission factor
        double dailyCO2ReductionKg = dailyEnergyKwh * req.getGridEmissionFactor();
        double monthlyCO2ReductionTonnes = (monthlyEnergyKwh * req.getGridEmissionFactor()) / 1000.0;
        double annualCO2ReductionTonnes = (annualEnergyKwh * req.getGridEmissionFactor()) / 1000.0;

        ORCCalculationResponse response = new ORCCalculationResponse();
        response.setRecoverableHeatKw(round(recoverableHeatKw));
        response.setOrcEfficiency(round(req.getOrcEfficiency()));
        response.setElectricalPowerKw(round(electricalPowerKw));
        response.setDailyEnergyKwh(round(dailyEnergyKwh));
        response.setMonthlyEnergyKwh(round(monthlyEnergyKwh));
        response.setAnnualEnergyKwh(round(annualEnergyKwh));
        response.setDailyCO2ReductionKg(round(dailyCO2ReductionKg));
        response.setMonthlyCO2ReductionTonnes(round(monthlyCO2ReductionTonnes));
        response.setAnnualCO2ReductionTonnes(round(annualCO2ReductionTonnes));
        return response;
    }

    /**
     * Engineering + business validation beyond the basic bean-validation
     * annotations already applied on the DTO (those enforce ranges; this
     * enforces cross-field relationships).
     */
    private void validate(ORCCalculationRequest req) {
        if (req.getInletTemperature() <= req.getOutletTemperature()) {
            throw new IllegalArgumentException("Inlet temperature must be greater than outlet temperature");
        }
        if (req.getFlowRate() <= 0) {
            throw new IllegalArgumentException("Flow rate must be greater than 0");
        }
        if (req.getSpecificHeat() <= 0) {
            throw new IllegalArgumentException("Specific heat must be greater than 0");
        }
        if (req.getHeatRecoveryFactor() < 0 || req.getHeatRecoveryFactor() > 100) {
            throw new IllegalArgumentException("Heat recovery factor must be between 0 and 100");
        }
        if (req.getOrcEfficiency() < 0 || req.getOrcEfficiency() > 100) {
            throw new IllegalArgumentException("ORC efficiency must be between 0 and 100");
        }
        if (req.getOperatingHours() < 1 || req.getOperatingHours() > 24) {
            throw new IllegalArgumentException("Operating hours must be between 1 and 24");
        }
        if (req.getOperatingDays() < 1 || req.getOperatingDays() > 31) {
            throw new IllegalArgumentException("Operating days must be between 1 and 31");
        }
        if (req.getGridEmissionFactor() < 0) {
            throw new IllegalArgumentException("Grid emission factor must be >= 0");
        }
    }

    /** Rounds a double to 3 decimal places for consistent, readable output. */
    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
