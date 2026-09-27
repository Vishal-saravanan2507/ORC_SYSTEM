package com.orc.wasteheat.dto;

import com.orc.wasteheat.entity.ORCPerformance;
import com.orc.wasteheat.entity.WasteHeatData;
import java.time.LocalDate;

/** Flattened view of a WasteHeatData record plus its ORCPerformance, for the frontend. */
public class WasteHeatResponse {

    private Long id;
    private Long industrialUnitId;
    private String industryName;
    private WasteHeatData.HeatSource heatSource;
    private Double flowRate;
    private Double inletTemperature;
    private Double outletTemperature;
    private Double specificHeat;
    private Double heatRecoveryFactor;
    private Integer operatingHours;
    private Integer operatingDays;
    private LocalDate recordedDate;

    private ORCCalculationResponse performance;

    public static WasteHeatResponse from(WasteHeatData data) {
        WasteHeatResponse r = new WasteHeatResponse();
        r.id = data.getId();
        r.industrialUnitId = data.getIndustrialUnit() != null ? data.getIndustrialUnit().getId() : null;
        r.industryName = data.getIndustrialUnit() != null ? data.getIndustrialUnit().getIndustryName() : null;
        r.heatSource = data.getHeatSource();
        r.flowRate = data.getFlowRate();
        r.inletTemperature = data.getInletTemperature();
        r.outletTemperature = data.getOutletTemperature();
        r.specificHeat = data.getSpecificHeat();
        r.heatRecoveryFactor = data.getHeatRecoveryFactor();
        r.operatingHours = data.getOperatingHours();
        r.operatingDays = data.getOperatingDays();
        r.recordedDate = data.getRecordedDate();

        ORCPerformance p = data.getPerformance();
        if (p != null) {
            ORCCalculationResponse perf = new ORCCalculationResponse();
            perf.setRecoverableHeatKw(p.getRecoverableHeatKw());
            perf.setOrcEfficiency(p.getOrcEfficiency());
            perf.setElectricalPowerKw(p.getElectricalPowerKw());
            perf.setDailyEnergyKwh(p.getDailyEnergyKwh());
            perf.setMonthlyEnergyKwh(p.getMonthlyEnergyKwh());
            perf.setAnnualEnergyKwh(p.getAnnualEnergyKwh());
            perf.setDailyCO2ReductionKg(p.getDailyCO2ReductionKg());
            perf.setMonthlyCO2ReductionTonnes(p.getMonthlyCO2ReductionTonnes());
            perf.setAnnualCO2ReductionTonnes(p.getAnnualCO2ReductionTonnes());
            r.performance = perf;
        }
        return r;
    }

    public Long getId() { return id; }
    public Long getIndustrialUnitId() { return industrialUnitId; }
    public String getIndustryName() { return industryName; }
    public WasteHeatData.HeatSource getHeatSource() { return heatSource; }
    public Double getFlowRate() { return flowRate; }
    public Double getInletTemperature() { return inletTemperature; }
    public Double getOutletTemperature() { return outletTemperature; }
    public Double getSpecificHeat() { return specificHeat; }
    public Double getHeatRecoveryFactor() { return heatRecoveryFactor; }
    public Integer getOperatingHours() { return operatingHours; }
    public Integer getOperatingDays() { return operatingDays; }
    public LocalDate getRecordedDate() { return recordedDate; }
    public ORCCalculationResponse getPerformance() { return performance; }
}
