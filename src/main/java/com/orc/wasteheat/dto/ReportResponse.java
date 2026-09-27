package com.orc.wasteheat.dto;

public class ReportResponse {

    private String industryName;
    private String startDate;
    private String endDate;
    private long numberOfRecords;

    private double totalHeatRecoveredKwh;
    private double totalElectricityGeneratedKwh;
    private double averageOrcEfficiency;
    private double totalCO2ReductionTonnes;
    private double averageTemperature;
    private double peakPowerKw;
    private double minimumPowerKw;
    private double maximumPowerKw;

    public String getIndustryName() { return industryName; }
    public void setIndustryName(String industryName) { this.industryName = industryName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public long getNumberOfRecords() { return numberOfRecords; }
    public void setNumberOfRecords(long numberOfRecords) { this.numberOfRecords = numberOfRecords; }

    public double getTotalHeatRecoveredKwh() { return totalHeatRecoveredKwh; }
    public void setTotalHeatRecoveredKwh(double totalHeatRecoveredKwh) { this.totalHeatRecoveredKwh = totalHeatRecoveredKwh; }

    public double getTotalElectricityGeneratedKwh() { return totalElectricityGeneratedKwh; }
    public void setTotalElectricityGeneratedKwh(double totalElectricityGeneratedKwh) { this.totalElectricityGeneratedKwh = totalElectricityGeneratedKwh; }

    public double getAverageOrcEfficiency() { return averageOrcEfficiency; }
    public void setAverageOrcEfficiency(double averageOrcEfficiency) { this.averageOrcEfficiency = averageOrcEfficiency; }

    public double getTotalCO2ReductionTonnes() { return totalCO2ReductionTonnes; }
    public void setTotalCO2ReductionTonnes(double totalCO2ReductionTonnes) { this.totalCO2ReductionTonnes = totalCO2ReductionTonnes; }

    public double getAverageTemperature() { return averageTemperature; }
    public void setAverageTemperature(double averageTemperature) { this.averageTemperature = averageTemperature; }

    public double getPeakPowerKw() { return peakPowerKw; }
    public void setPeakPowerKw(double peakPowerKw) { this.peakPowerKw = peakPowerKw; }

    public double getMinimumPowerKw() { return minimumPowerKw; }
    public void setMinimumPowerKw(double minimumPowerKw) { this.minimumPowerKw = minimumPowerKw; }

    public double getMaximumPowerKw() { return maximumPowerKw; }
    public void setMaximumPowerKw(double maximumPowerKw) { this.maximumPowerKw = maximumPowerKw; }
}
