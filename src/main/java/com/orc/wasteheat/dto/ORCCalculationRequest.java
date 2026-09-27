package com.orc.wasteheat.dto;

import jakarta.validation.constraints.*;

/**
 * Input payload for POST /api/orc/calculate.
 * Also reused internally whenever a WasteHeatData record needs its
 * performance figures (re)computed.
 */
public class ORCCalculationRequest {

    @NotNull @Positive(message = "Flow rate must be greater than 0")
    private Double flowRate; // kg/s

    @NotNull
    private Double inletTemperature; // deg C

    @NotNull
    private Double outletTemperature; // deg C

    @NotNull @Positive(message = "Specific heat must be greater than 0")
    private Double specificHeat; // kJ/kg.K

    @NotNull @DecimalMin(value = "0.0", message = "Heat recovery factor must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Heat recovery factor must be between 0 and 100")
    private Double heatRecoveryFactor; // %

    @NotNull @DecimalMin(value = "0.0", message = "ORC efficiency must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "ORC efficiency must be between 0 and 100")
    private Double orcEfficiency; // %

    @NotNull @Min(value = 1, message = "Operating hours must be between 1 and 24")
    @Max(value = 24, message = "Operating hours must be between 1 and 24")
    private Integer operatingHours;

    @NotNull @Min(value = 1, message = "Operating days must be between 1 and 31")
    @Max(value = 31, message = "Operating days must be between 1 and 31")
    private Integer operatingDays;

    @NotNull @DecimalMin(value = "0.0", message = "Grid emission factor must be >= 0")
    private Double gridEmissionFactor; // kg CO2/kWh

    public ORCCalculationRequest() {
    }

    // ---- Getters / Setters ----

    public Double getFlowRate() { return flowRate; }
    public void setFlowRate(Double flowRate) { this.flowRate = flowRate; }

    public Double getInletTemperature() { return inletTemperature; }
    public void setInletTemperature(Double inletTemperature) { this.inletTemperature = inletTemperature; }

    public Double getOutletTemperature() { return outletTemperature; }
    public void setOutletTemperature(Double outletTemperature) { this.outletTemperature = outletTemperature; }

    public Double getSpecificHeat() { return specificHeat; }
    public void setSpecificHeat(Double specificHeat) { this.specificHeat = specificHeat; }

    public Double getHeatRecoveryFactor() { return heatRecoveryFactor; }
    public void setHeatRecoveryFactor(Double heatRecoveryFactor) { this.heatRecoveryFactor = heatRecoveryFactor; }

    public Double getOrcEfficiency() { return orcEfficiency; }
    public void setOrcEfficiency(Double orcEfficiency) { this.orcEfficiency = orcEfficiency; }

    public Integer getOperatingHours() { return operatingHours; }
    public void setOperatingHours(Integer operatingHours) { this.operatingHours = operatingHours; }

    public Integer getOperatingDays() { return operatingDays; }
    public void setOperatingDays(Integer operatingDays) { this.operatingDays = operatingDays; }

    public Double getGridEmissionFactor() { return gridEmissionFactor; }
    public void setGridEmissionFactor(Double gridEmissionFactor) { this.gridEmissionFactor = gridEmissionFactor; }
}
