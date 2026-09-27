package com.orc.wasteheat.dto;

import java.util.List;

public class DashboardResponse {

    private long totalIndustrialUnits;
    private long totalActiveUnits;
    private double totalWasteHeatRecoveredKw;
    private double currentEstimatedPowerKw;
    private double dailyEnergyGeneratedKwh;
    private double monthlyEnergyGeneratedKwh;
    private double annualEnergyPotentialKwh;
    private double monthlyCO2ReductionTonnes;
    private double averageOrcEfficiency;

    private List<ChartPoint> powerTrend;
    private List<ChartPoint> energyTrend;
    private List<ChartPoint> temperatureTrend;
    private List<ChartPoint> efficiencyTrend;

    private List<RecentMeasurement> recentMeasurements;

    public static class ChartPoint {
        private String label; // date
        private Double value;

        public ChartPoint() {}
        public ChartPoint(String label, Double value) { this.label = label; this.value = value; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    public static class RecentMeasurement {
        private Long id;
        private String industryName;
        private String heatSource;
        private String recordedDate;
        private Double electricalPowerKw;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getIndustryName() { return industryName; }
        public void setIndustryName(String industryName) { this.industryName = industryName; }
        public String getHeatSource() { return heatSource; }
        public void setHeatSource(String heatSource) { this.heatSource = heatSource; }
        public String getRecordedDate() { return recordedDate; }
        public void setRecordedDate(String recordedDate) { this.recordedDate = recordedDate; }
        public Double getElectricalPowerKw() { return electricalPowerKw; }
        public void setElectricalPowerKw(Double electricalPowerKw) { this.electricalPowerKw = electricalPowerKw; }
    }

    // ---- Getters / Setters ----

    public long getTotalIndustrialUnits() { return totalIndustrialUnits; }
    public void setTotalIndustrialUnits(long totalIndustrialUnits) { this.totalIndustrialUnits = totalIndustrialUnits; }

    public long getTotalActiveUnits() { return totalActiveUnits; }
    public void setTotalActiveUnits(long totalActiveUnits) { this.totalActiveUnits = totalActiveUnits; }

    public double getTotalWasteHeatRecoveredKw() { return totalWasteHeatRecoveredKw; }
    public void setTotalWasteHeatRecoveredKw(double totalWasteHeatRecoveredKw) { this.totalWasteHeatRecoveredKw = totalWasteHeatRecoveredKw; }

    public double getCurrentEstimatedPowerKw() { return currentEstimatedPowerKw; }
    public void setCurrentEstimatedPowerKw(double currentEstimatedPowerKw) { this.currentEstimatedPowerKw = currentEstimatedPowerKw; }

    public double getDailyEnergyGeneratedKwh() { return dailyEnergyGeneratedKwh; }
    public void setDailyEnergyGeneratedKwh(double dailyEnergyGeneratedKwh) { this.dailyEnergyGeneratedKwh = dailyEnergyGeneratedKwh; }

    public double getMonthlyEnergyGeneratedKwh() { return monthlyEnergyGeneratedKwh; }
    public void setMonthlyEnergyGeneratedKwh(double monthlyEnergyGeneratedKwh) { this.monthlyEnergyGeneratedKwh = monthlyEnergyGeneratedKwh; }

    public double getAnnualEnergyPotentialKwh() { return annualEnergyPotentialKwh; }
    public void setAnnualEnergyPotentialKwh(double annualEnergyPotentialKwh) { this.annualEnergyPotentialKwh = annualEnergyPotentialKwh; }

    public double getMonthlyCO2ReductionTonnes() { return monthlyCO2ReductionTonnes; }
    public void setMonthlyCO2ReductionTonnes(double monthlyCO2ReductionTonnes) { this.monthlyCO2ReductionTonnes = monthlyCO2ReductionTonnes; }

    public double getAverageOrcEfficiency() { return averageOrcEfficiency; }
    public void setAverageOrcEfficiency(double averageOrcEfficiency) { this.averageOrcEfficiency = averageOrcEfficiency; }

    public List<ChartPoint> getPowerTrend() { return powerTrend; }
    public void setPowerTrend(List<ChartPoint> powerTrend) { this.powerTrend = powerTrend; }

    public List<ChartPoint> getEnergyTrend() { return energyTrend; }
    public void setEnergyTrend(List<ChartPoint> energyTrend) { this.energyTrend = energyTrend; }

    public List<ChartPoint> getTemperatureTrend() { return temperatureTrend; }
    public void setTemperatureTrend(List<ChartPoint> temperatureTrend) { this.temperatureTrend = temperatureTrend; }

    public List<ChartPoint> getEfficiencyTrend() { return efficiencyTrend; }
    public void setEfficiencyTrend(List<ChartPoint> efficiencyTrend) { this.efficiencyTrend = efficiencyTrend; }

    public List<RecentMeasurement> getRecentMeasurements() { return recentMeasurements; }
    public void setRecentMeasurements(List<RecentMeasurement> recentMeasurements) { this.recentMeasurements = recentMeasurements; }
}
