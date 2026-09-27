package com.orc.wasteheat.dto;

/**
 * Result of an ORC estimation calculation. All values are simplified
 * engineering ESTIMATES suitable for a college project, not certified
 * industrial design figures.
 */
public class ORCCalculationResponse {

    private String label = "Estimated / Simulation Result";

    private Double recoverableHeatKw;
    private Double orcEfficiency;
    private Double electricalPowerKw;
    private Double dailyEnergyKwh;
    private Double monthlyEnergyKwh;
    private Double annualEnergyKwh;
    private Double dailyCO2ReductionKg;
    private Double monthlyCO2ReductionTonnes;
    private Double annualCO2ReductionTonnes;

    public ORCCalculationResponse() {
    }

    public ORCCalculationResponse(Double recoverableHeatKw, Double orcEfficiency, Double electricalPowerKw,
                                   Double dailyEnergyKwh, Double monthlyEnergyKwh, Double annualEnergyKwh,
                                   Double dailyCO2ReductionKg, Double monthlyCO2ReductionTonnes,
                                   Double annualCO2ReductionTonnes) {
        this.recoverableHeatKw = recoverableHeatKw;
        this.orcEfficiency = orcEfficiency;
        this.electricalPowerKw = electricalPowerKw;
        this.dailyEnergyKwh = dailyEnergyKwh;
        this.monthlyEnergyKwh = monthlyEnergyKwh;
        this.annualEnergyKwh = annualEnergyKwh;
        this.dailyCO2ReductionKg = dailyCO2ReductionKg;
        this.monthlyCO2ReductionTonnes = monthlyCO2ReductionTonnes;
        this.annualCO2ReductionTonnes = annualCO2ReductionTonnes;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

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
}
