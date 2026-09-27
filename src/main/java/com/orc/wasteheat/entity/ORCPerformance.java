package com.orc.wasteheat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orc_performance")
public class ORCPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_heat_data_id", nullable = false, unique = true)
    @JsonIgnore
    private WasteHeatData wasteHeatData;

    private Double recoverableHeatKw;
    private Double orcEfficiency;
    private Double electricalPowerKw;
    private Double dailyEnergyKwh;
    private Double monthlyEnergyKwh;
    private Double annualEnergyKwh;
    private Double dailyCO2ReductionKg;
    private Double monthlyCO2ReductionTonnes;
    private Double annualCO2ReductionTonnes;

    private LocalDateTime calculatedAt;

    public ORCPerformance() {
    }

    @PrePersist
    public void prePersist() {
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WasteHeatData getWasteHeatData() { return wasteHeatData; }
    public void setWasteHeatData(WasteHeatData wasteHeatData) { this.wasteHeatData = wasteHeatData; }

    public Double getRecoverableHeatKw() { return recoverableHeatKw; }
    public void setRecoverableHeatKw(Double recoverableHeatKw) { this.recoverableHeatKw = recoverableHeatKw; }

    public Double getOrcEfficiency() { return orcEfficiency; }
    public void setOrcEfficiency(Double orcEfficiency) { this.orcEfficiency = orcEfficiency; }

    public Double getElectricalPowerKw() { return electricalPowerKw; }
    public void setElectricalPowerKw(Double electricalPowerKw) { this.electricalPowerKw = electricalPowerKw; }

    public Double getDailyEnergyKwh() { return dailyEnergyKwh; }
    public void setDailyEnergyKwh(Double dailyEnergyKwh) { this.dailyEnergyKwh = dailyEnergyKwh; }

    public Double getMonthlyEnergyKwh() { return monthlyEnergyKwh; }
    public void setMonthlyEnergyKwh(Double monthlyEnergyKwh) { this.monthlyEnergyKwh = monthlyEnergyKwh; }

    public Double getAnnualEnergyKwh() { return annualEnergyKwh; }
    public void setAnnualEnergyKwh(Double annualEnergyKwh) { this.annualEnergyKwh = annualEnergyKwh; }

    public Double getDailyCO2ReductionKg() { return dailyCO2ReductionKg; }
    public void setDailyCO2ReductionKg(Double dailyCO2ReductionKg) { this.dailyCO2ReductionKg = dailyCO2ReductionKg; }

    public Double getMonthlyCO2ReductionTonnes() { return monthlyCO2ReductionTonnes; }
    public void setMonthlyCO2ReductionTonnes(Double monthlyCO2ReductionTonnes) { this.monthlyCO2ReductionTonnes = monthlyCO2ReductionTonnes; }

    public Double getAnnualCO2ReductionTonnes() { return annualCO2ReductionTonnes; }
    public void setAnnualCO2ReductionTonnes(Double annualCO2ReductionTonnes) { this.annualCO2ReductionTonnes = annualCO2ReductionTonnes; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
