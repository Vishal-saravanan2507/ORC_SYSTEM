package com.orc.wasteheat.dto;

import com.orc.wasteheat.entity.WasteHeatData;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Incoming payload for creating/updating a waste heat measurement. */
public class WasteHeatRequest {

    @NotNull(message = "Industrial unit is required")
    private Long industrialUnitId;

    @NotNull(message = "Heat source is required")
    private WasteHeatData.HeatSource heatSource;

    @NotNull @Positive(message = "Flow rate must be greater than 0")
    private Double flowRate;

    @NotNull
    private Double inletTemperature;

    @NotNull
    private Double outletTemperature;

    @NotNull @Positive(message = "Specific heat must be greater than 0")
    private Double specificHeat;

    @NotNull @DecimalMin(value = "0.0", message = "Heat recovery factor must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Heat recovery factor must be between 0 and 100")
    private Double heatRecoveryFactor;

    @NotNull @Min(value = 1, message = "Operating hours must be between 1 and 24")
    @Max(value = 24, message = "Operating hours must be between 1 and 24")
    private Integer operatingHours;

    @NotNull @Min(value = 1, message = "Operating days must be between 1 and 31")
    @Max(value = 31, message = "Operating days must be between 1 and 31")
    private Integer operatingDays;

    /** ORC efficiency (%) and grid emission factor are needed to compute performance too. */
    @NotNull @DecimalMin(value = "0.0", message = "ORC efficiency must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "ORC efficiency must be between 0 and 100")
    private Double orcEfficiency;

    @NotNull @DecimalMin(value = "0.0", message = "Grid emission factor must be >= 0")
    private Double gridEmissionFactor;

    private LocalDate recordedDate;

    public Long getIndustrialUnitId() { return industrialUnitId; }
    public void setIndustrialUnitId(Long industrialUnitId) { this.industrialUnitId = industrialUnitId; }

    public WasteHeatData.HeatSource getHeatSource() { return heatSource; }
    public void setHeatSource(WasteHeatData.HeatSource heatSource) { this.heatSource = heatSource; }

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

    public Integer getOperatingHours() { return operatingHours; }
    public void setOperatingHours(Integer operatingHours) { this.operatingHours = operatingHours; }

    public Integer getOperatingDays() { return operatingDays; }
    public void setOperatingDays(Integer operatingDays) { this.operatingDays = operatingDays; }

    public Double getOrcEfficiency() { return orcEfficiency; }
    public void setOrcEfficiency(Double orcEfficiency) { this.orcEfficiency = orcEfficiency; }

    public Double getGridEmissionFactor() { return gridEmissionFactor; }
    public void setGridEmissionFactor(Double gridEmissionFactor) { this.gridEmissionFactor = gridEmissionFactor; }

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }
}
