package com.orc.wasteheat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "waste_heat_data")
public class WasteHeatData {

    public enum HeatSource {
        FURNACE, BOILER, KILN, EXHAUST_GAS, GAS_TURBINE, ENGINE, PROCESS_HEATER, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industrial_unit_id", nullable = false)
    @JsonIgnore
    private IndustrialUnit industrialUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeatSource heatSource;

    @NotNull @Positive(message = "Flow rate must be greater than 0")
    private Double flowRate; // kg/s

    @NotNull
    private Double inletTemperature; // deg C

    @NotNull
    private Double outletTemperature; // deg C

    @NotNull @Positive(message = "Specific heat must be greater than 0")
    private Double specificHeat; // kJ/kg.K

    @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private Double heatRecoveryFactor; // %

    @NotNull @Min(1) @Max(24)
    private Integer operatingHours; // hours/day

    @NotNull @Min(1) @Max(31)
    private Integer operatingDays; // days/month

    private LocalDate recordedDate;

    @OneToOne(mappedBy = "wasteHeatData", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ORCPerformance performance;

    public WasteHeatData() {
    }

    @PrePersist
    public void prePersist() {
        if (recordedDate == null) {
            recordedDate = LocalDate.now();
        }
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IndustrialUnit getIndustrialUnit() { return industrialUnit; }
    public void setIndustrialUnit(IndustrialUnit industrialUnit) { this.industrialUnit = industrialUnit; }

    public HeatSource getHeatSource() { return heatSource; }
    public void setHeatSource(HeatSource heatSource) { this.heatSource = heatSource; }

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

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }

    public ORCPerformance getPerformance() { return performance; }
    public void setPerformance(ORCPerformance performance) { this.performance = performance; }
}
